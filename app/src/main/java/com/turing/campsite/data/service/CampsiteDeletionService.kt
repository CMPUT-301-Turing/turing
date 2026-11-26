/*
 * AI Assistance Disclosure
 *
 * Company: OpenAI
 * Tool: ChatGPT (GPT-6)
 * Date: 2026-11-25
 * Subject: Admin campsite removal, dependency checks,
 *          and separation of deletion policy from data access.
 *
 * Scope: AI-assisted implementation of the campsite deletion service.
 */

package com.turing.campsite.data.service

import com.google.firebase.Timestamp
import com.turing.campsite.data.model.Lottery
import com.turing.campsite.data.repository.CampsiteRepository
import com.turing.campsite.data.repository.LotteryRepository
import com.turing.campsite.data.repository.ReservationRepository

/**
 * Outcomes that the admin UI can handle after requesting campsite deletion.
 *
 * Database failures are thrown to the caller rather than reported as a
 * successful or policy-blocked deletion.
 */
sealed interface CampsiteDeletionResult {
    data object Deleted : CampsiteDeletionResult
    data object BlockedByLottery : CampsiteDeletionResult
    data object BlockedByReservation : CampsiteDeletionResult
}

/**
 * Coordinates campsite removal across the campsite, lottery, and reservation
 * repositories. This is the place to extend the policy later with cancellation
 * and notifications, without changing the admin screen.
 */
class CampsiteDeletionService(
    private val campsiteRepository: CampsiteRepository = CampsiteRepository(),
    private val lotteryRepository: LotteryRepository = LotteryRepository(),
    private val reservationRepository: ReservationRepository = ReservationRepository(),
    private val now: () -> Timestamp = { Timestamp.now() }
) {

    /**
     * Deletes a campsite and its reviews unless it has an active lottery,
     * an open booking period, or an active reservation.
     *
     * Repository errors propagate to the caller.
     */
    suspend fun delete(campsiteId: String): CampsiteDeletionResult {
        require(campsiteId.isNotBlank()) { "Campsite ID cannot be blank" }

        val currentTime = now()
        val lotteries = lotteryRepository.listByCampsite(campsiteId)

        if (lotteries.any { it.blocksDeletion(currentTime) }) {
            return CampsiteDeletionResult.BlockedByLottery
        }

        if (reservationRepository.listActiveForCampsite(campsiteId).isNotEmpty()) {
            return CampsiteDeletionResult.BlockedByReservation
        }

        campsiteRepository.deleteWithReviews(campsiteId)
        return CampsiteDeletionResult.Deleted
    }

    private fun Lottery.blocksDeletion(currentTime: Timestamp): Boolean {
        val activeLottery =
            status.equals("open", ignoreCase = true) ||
                status.equals("drawn", ignoreCase = true)

        val start = bookingStart
        val end = bookingEnd
        val bookingInProgress =
            start != null && end != null &&
                currentTime >= start && currentTime <= end

        return activeLottery || bookingInProgress
    }
}
