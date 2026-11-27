/*
 * AI Assistance Disclosure
 * OpenAI, ChatGPT (GPT-6), 2026-11-26.
 * Scope: Coordinated campsite deletion, recovery, authorization and notifications.
 */
package com.turing.campsite.data.service

import com.google.firebase.auth.FirebaseAuth
import com.turing.campsite.data.model.Campsite
import com.turing.campsite.data.model.Notification
import com.turing.campsite.data.model.NotificationType
import com.turing.campsite.data.model.UserRole
import com.turing.campsite.data.repository.CampsiteRepository
import com.turing.campsite.data.repository.CampsiteLotteryRemoval
import com.turing.campsite.data.repository.LotteryRepository
import com.turing.campsite.data.repository.MessageRepository
import com.turing.campsite.data.repository.NotificationRepository
import com.turing.campsite.data.repository.ReservationRepository
import com.turing.campsite.data.repository.UserRepository
import kotlinx.coroutines.CancellationException

sealed interface CampsiteDeletionResult {
    data object Deleted : CampsiteDeletionResult
}

/** Delivery must be retry-safe; a missing implementation must fail, never silently succeed. */
fun interface CampsiteRemovalNotifier {
    suspend fun notify(campsite: Campsite, recipientIds: List<String>, actorId: String)
}

/** The notification and its delivery marker are committed together for each recipient. */
class FirestoreCampsiteRemovalNotifier(
    private val repository: NotificationRepository = NotificationRepository()
) : CampsiteRemovalNotifier {
    override suspend fun notify(campsite: Campsite, recipientIds: List<String>, actorId: String) {
        for (recipientId in recipientIds) {
            repository.sendCampsiteRemoval(campsite.id, Notification(
                toUserId = recipientId,
                fromUserId = actorId,
                type = NotificationType.CAMPSITE_REMOVED,
                title = "Campsite removed",
                body = "${campsite.name} has been removed, along with its reservations. " +
                    "Lotteries with other campsites continue without this campsite; lotteries with none remaining are removed."
            ))
        }
    }
}

/** Cleanup may span several writes. The parent and saved recipients remain until completion. */
class CampsiteDeletionService(
    private val campsiteRepository: CampsiteRepository = CampsiteRepository(),
    private val lotteryRepository: LotteryRepository = LotteryRepository(),
    private val reservationRepository: ReservationRepository = ReservationRepository(),
    private val messageRepository: MessageRepository = MessageRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val notifier: CampsiteRemovalNotifier = FirestoreCampsiteRemovalNotifier(),
    private val signedInUid: () -> String? = { FirebaseAuth.getInstance().currentUser?.uid }
) {
    /** Normal dependencies are cleaned up, never treated as reasons to block deletion. */
    suspend fun delete(campsiteId: String): CampsiteDeletionResult {
        require(campsiteId.isNotBlank()) { "Campsite ID cannot be blank" }
        val actorId = signedInUid() ?: throw SecurityException("Sign in to delete a campsite")
        val actor = userRepository.get(actorId)
            ?: throw SecurityException("Your profile could not be found")
        val campsite = campsiteRepository.get(campsiteId)
            ?: throw IllegalStateException("Campsite not found; refresh the campsite list")
        if (!actor.hasRole(UserRole.ADMIN) &&
            !(actor.hasRole(UserRole.OWNER) && campsite.ownerId == actorId)) {
            throw SecurityException("Only an administrator or this campsite's owner can delete it")
        }

        check(campsite.ownerId.isNotBlank()) { "Campsite has no owner" }

        // No destructive work before both the gate and recipient snapshot are persisted.
        try {
            var plan = campsiteRepository.beginDeletion(campsiteId)
            if (plan == null) {
                val lotteries = lotteryRepository.listByCampsiteForDeletion(campsiteId)
                // Freeze every affected lottery before reading participants, including shared ones.
                lotteries.forEach { lotteryRepository.beginCampsiteRemoval(it.id, campsiteId) }
                val reservations = (reservationRepository.listByCampsite(campsiteId) +
                    lotteries.flatMap { reservationRepository.listByLottery(it.id) }).distinctBy { it.id }
                val entrantIds = lotteries.flatMap { lotteryRepository.listParticipantIdsForRemoval(it.id) } +
                    reservations.map { it.entrantId }
                val recipients = (listOf(campsite.ownerId) + entrantIds).filter { it.isNotBlank() }.distinct()
                plan = campsiteRepository.captureDeletionRecipients(campsiteId, recipients, lotteries.map { it.id })
            }

            // Other campsites' reservations in shared lotteries must survive.
            for (reservation in reservationRepository.listByCampsite(campsiteId)) {
                messageRepository.deleteForReservation(reservation.id)
                reservationRepository.delete(reservation.id)
            }
            // Saved IDs remain usable even after this campsite has been detached on an earlier attempt.
            for (lotteryId in plan.lotteryIds) {
                when (lotteryRepository.removeCampsite(lotteryId, campsiteId)) {
                    CampsiteLotteryRemoval.DELETE_LOTTERY -> {
                        lotteryRepository.deleteEntries(lotteryId)
                        lotteryRepository.deleteForCampsiteRemoval(lotteryId, campsiteId)
                    }
                    CampsiteLotteryRemoval.RETAINED, CampsiteLotteryRemoval.ALREADY_REMOVED -> Unit
                }
            }
            notifier.notify(campsite, plan.recipientIds, actorId)
            campsiteRepository.deleteReviews(campsiteId)
            campsiteRepository.delete(campsiteId)
            return CampsiteDeletionResult.Deleted
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (failure: Exception) {
            throw IllegalStateException(
                "Campsite deletion did not finish. Cleanup may be partial; retry deletion to continue. " +
                    (failure.message ?: "Database operation failed"), failure
            )
        }
    }
}
