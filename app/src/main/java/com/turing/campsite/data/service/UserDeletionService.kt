/* AI Assistance Disclosure: OpenAI, ChatGPT (GPT-6), 2026-11-27.
 * Scope: Coordinated self/admin profile removal with permanent UID blocking and retry checkpoints.
 */
package com.turing.campsite.data.service

import com.google.firebase.auth.FirebaseAuth
import com.turing.campsite.data.model.Notification
import com.turing.campsite.data.model.NotificationType
import com.turing.campsite.data.model.UserRole
import com.turing.campsite.data.repository.*
import kotlinx.coroutines.CancellationException

sealed interface UserDeletionResult {
    /** Firestore profile removed and UID blocked; Firebase Authentication identity is retained. */
    data object ProfileRemoved : UserDeletionResult
}

fun interface UserRemovalNotifier {
    suspend fun notify(uid: String, plan: UserDeletionPlan)
}

class FirestoreUserRemovalNotifier(
    private val notifications: NotificationRepository = NotificationRepository()
) : UserRemovalNotifier {
    override suspend fun notify(uid: String, plan: UserDeletionPlan) {
        for (recipient in plan.recipientIds) {
            val lotteryRemoved = recipient in plan.entrantRecipientIds
            notifications.sendUserRemoval(uid, Notification(
                toUserId = recipient,
                type = NotificationType.USER_REMOVED,
                title = if (lotteryRemoved) "Lottery or reservation removed" else "Related profile removed",
                body = if (lotteryRemoved) "A related lottery with no campsites remaining, or a reservation involving a removed owner, has been removed."
                    else "A profile associated with your lottery or reservation has been removed. Its related bookings have been removed; shared lotteries with remaining campsites continue.",
                userDeletionId = uid
            ))
        }
    }
}

/** One cascade for all role combinations. Authorization differs for self versus administrator requests. */
class UserDeletionService(
    private val users: UserRepository = UserRepository(),
    private val campsites: CampsiteRepository = CampsiteRepository(),
    private val lotteries: LotteryRepository = LotteryRepository(),
    private val reservations: ReservationRepository = ReservationRepository(),
    private val messages: MessageRepository = MessageRepository(),
    private val notifications: NotificationRepository = NotificationRepository(),
    campsiteDeletion: CampsiteDeletionService? = null,
    private val notifier: UserRemovalNotifier = FirestoreUserRemovalNotifier(),
    private val signedInUid: () -> String? = { FirebaseAuth.getInstance().currentUser?.uid },
    private val chooseOwner: (List<String>) -> String = { it.random() }
) {
    private val campsiteDeletion by lazy {
        campsiteDeletion ?: CampsiteDeletionService(campsiteRepository = campsites,
            lotteryRepository = lotteries, reservationRepository = reservations,
            messageRepository = messages, userRepository = users, signedInUid = signedInUid)
    }

    suspend fun delete(uid: String): UserDeletionResult {
        require(uid.isNotBlank()) { "User ID cannot be blank" }
        val actorId = signedInUid() ?: throw SecurityException("Sign in to remove a profile")
        val state = users.deletionState(uid)
        if (actorId == uid && state?.status == "DELETED") return UserDeletionResult.ProfileRemoved
        val target = users.getForDeletion(uid)
        val wasAdmin = target?.hasRole(UserRole.ADMIN) ?: state?.wasAdmin ?: false
        if (actorId != uid) {
            val actor = users.getForDeletion(actorId)
            if (actor?.hasRole(UserRole.ADMIN) != true || users.deletionState(actorId) != null) {
                throw SecurityException("Only an administrator can remove another profile")
            }
            if (wasAdmin) throw SecurityException("Administrators can only remove their own admin profile")
        }
        if (state?.status == "DELETED") return UserDeletionResult.ProfileRemoved
        check(target != null) { "Profile not found" }

        try {
            var plan = users.beginDeletion(uid, wasAdmin).plan
            if (plan == null) {
                val owned = campsites.listOwnedForUserDeletion(uid)
                val bookings = reservations.listForUserDeletion(uid)
                val linked = lotteries.listForUserDeletion(uid, owned.map { it.id }, bookings.map { it.lotteryId })
                val owners = mutableSetOf<String>()
                val entrants = mutableSetOf<String>()
                owners += bookings.filter { it.entrantId == uid }.map { it.ownerId }
                // A copied owner relationship can outlive campsite ownership; those bookings still need resolution.
                entrants += bookings.filter { it.ownerId == uid && it.campsiteId !in owned.map { c -> c.id } }.map { it.entrantId }
                for (lottery in linked) {
                    owners += lottery.ownerId
                    owners += lottery.ownerIds
                    for (id in lottery.campsiteIds) campsites.getForUserDeletion(id)?.let { owners += it.ownerId }
                    if (lottery.ownerId == uid && lottery.campsiteIds.isEmpty()) entrants += lottery.participantIds
                }
                owners.remove(uid); entrants.remove(uid)
                plan = users.captureDeletionPlan(uid, UserDeletionPlan(
                    owned.map { it.id }, linked.map { it.id }, bookings.map { it.id },
                    campsites.reviewCampsitesForUserDeletion(uid),
                    owners.filter { it.isNotBlank() }, entrants.filter { it.isNotBlank() }))
            }

            for (lotteryId in plan.lotteryIds) {
                val info = lotteries.userDeletionInfo(lotteryId) ?: continue
                val survivingOwners = info.campsiteIds.mapNotNull { campsites.getForUserDeletion(it)?.ownerId }
                    .filter { it != uid }.distinct()
                var successor: String? = null
                if (info.ownerId == uid && survivingOwners.isNotEmpty()) {
                    val eligible = survivingOwners.filter { owner ->
                        users.deletionState(owner) == null && users.getForDeletion(owner)?.hasRole(UserRole.OWNER) == true
                    }
                    check(eligible.isNotEmpty()) { "No eligible surviving lottery owner; retry after related account cleanup" }
                    val previous = users.successor(uid, lotteryId)
                    successor = users.saveSuccessor(uid, lotteryId,
                        if (previous in eligible) requireNotNull(previous) else chooseOwner(eligible))
                }
                lotteries.removeUserOwnership(lotteryId, uid, successor)
                lotteries.removeUserParticipation(lotteryId, uid)
                if (info.ownerId == uid && info.campsiteIds.isEmpty()) lotteries.deleteEmptyOwnedLottery(lotteryId, uid)
            }

            for (id in plan.campsiteIds) {
                if (campsites.getForUserDeletion(id) != null) campsiteDeletion.delete(id)
            }

            for (id in plan.reservationIds) {
                if (reservations.getForUserDeletion(id) != null) {
                    messages.deleteForReservation(id)
                    reservations.delete(id)
                }
            }

            for (id in plan.reviewCampsiteIds) campsites.deleteReviewsByUser(id, uid)
            messages.deleteForUser(uid)
            notifications.deleteForUser(uid)
            notifier.notify(uid, plan)
            users.finishDeletion(uid)
            return UserDeletionResult.ProfileRemoved
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (failure: Exception) {
            throw IllegalStateException("Profile deletion did not finish. This UID remains blocked; retry to continue cleanup. " +
                (failure.message ?: "Database operation failed"), failure)
        }
    }
}
