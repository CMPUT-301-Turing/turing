/*
 * AI Assistance Disclosure
 *
 * Company: OpenAI
 * Tool: ChatGPT (GPT-6)
 *
 * Subjects:
 * 2026-11-25: Campsite deletion policy unit tests.
 *
 * Scope: AI-assisted implementation, debugging, and code review.
 */

package com.turing.campsite.data.service

import com.google.firebase.Timestamp
import com.turing.campsite.data.model.Lottery
import com.turing.campsite.data.model.Reservation
import com.turing.campsite.data.repository.CampsiteRepository
import com.turing.campsite.data.repository.LotteryRepository
import com.turing.campsite.data.repository.ReservationRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify

/**
 * Unit tests for [CampsiteDeletionService]. Repositories are mocked, so these
 * tests verify deletion policy and coordination, not Firestore permissions.
 */
class CampsiteDeletionServiceTest {

    private val campsiteId = "campsite-1"
    private val currentTime = Timestamp(1_800_000_000L, 0)
    private val beforeBooking = Timestamp(1_799_000_000L, 0)
    private val afterBooking = Timestamp(1_801_000_000L, 0)

    private lateinit var campsiteRepository: CampsiteRepository
    private lateinit var lotteryRepository: LotteryRepository
    private lateinit var reservationRepository: ReservationRepository
    private lateinit var service: CampsiteDeletionService

    @Before
    fun setUp() = runBlocking<Unit> {
        campsiteRepository = mock(CampsiteRepository::class.java)
        lotteryRepository = mock(LotteryRepository::class.java)
        reservationRepository = mock(ReservationRepository::class.java)

        service = CampsiteDeletionService(
            campsiteRepository = campsiteRepository,
            lotteryRepository = lotteryRepository,
            reservationRepository = reservationRepository,
            now = { currentTime }
        )

        // By default there are no dependencies preventing deletion.
        doReturn(emptyList<Lottery>()).`when`(lotteryRepository).listByCampsite(campsiteId)
        doReturn(emptyList<Reservation>()).`when`(reservationRepository)
            .listActiveForCampsite(campsiteId)
    }

    @Test
    fun delete_noDependencies_deletesCampsiteAndReviews() = runBlocking<Unit> {
        val result = service.delete(campsiteId)

        assertEquals(CampsiteDeletionResult.Deleted, result)
        verify(lotteryRepository).listByCampsite(campsiteId)
        verify(reservationRepository).listActiveForCampsite(campsiteId)
        verify(campsiteRepository).deleteWithReviews(campsiteId)
    }

    @Test
    fun delete_openLottery_blocksDeletion() = runBlocking<Unit> {
        doReturn(listOf(lottery(status = "open"))).`when`(lotteryRepository)
            .listByCampsite(campsiteId)

        val result = service.delete(campsiteId)

        assertEquals(CampsiteDeletionResult.BlockedByLottery, result)
        verify(reservationRepository, never()).listActiveForCampsite(campsiteId)
        verify(campsiteRepository, never()).deleteWithReviews(campsiteId)
    }

    @Test
    fun delete_drawnLottery_blocksEvenIfBookingPeriodEnded() = runBlocking<Unit> {
        doReturn(
            listOf(lottery(status = "drawn", start = beforeBooking, end = beforeBooking))
        ).`when`(lotteryRepository).listByCampsite(campsiteId)

        val result = service.delete(campsiteId)

        assertEquals(CampsiteDeletionResult.BlockedByLottery, result)
        verify(campsiteRepository, never()).deleteWithReviews(campsiteId)
    }

    @Test
    fun delete_closedLotteryDuringBookingPeriod_blocksDeletion() = runBlocking<Unit> {
        doReturn(
            listOf(lottery(status = "closed", start = beforeBooking, end = afterBooking))
        ).`when`(lotteryRepository).listByCampsite(campsiteId)

        val result = service.delete(campsiteId)

        assertEquals(CampsiteDeletionResult.BlockedByLottery, result)
        verify(campsiteRepository, never()).deleteWithReviews(campsiteId)
    }

    @Test
    fun delete_closedLotteryAfterBookingPeriod_succeeds() = runBlocking<Unit> {
        doReturn(
            listOf(lottery(status = "closed", start = beforeBooking, end = beforeBooking))
        ).`when`(lotteryRepository).listByCampsite(campsiteId)

        val result = service.delete(campsiteId)

        assertEquals(CampsiteDeletionResult.Deleted, result)
        verify(campsiteRepository).deleteWithReviews(campsiteId)
    }

    @Test
    fun delete_oneBlockingLotteryAmongSeveral_blocksDeletion() = runBlocking<Unit> {
        doReturn(
            listOf(
                lottery(status = "closed", start = beforeBooking, end = beforeBooking),
                lottery(status = "OPEN")
            )
        ).`when`(lotteryRepository).listByCampsite(campsiteId)

        val result = service.delete(campsiteId)

        assertEquals(CampsiteDeletionResult.BlockedByLottery, result)
        verify(campsiteRepository, never()).deleteWithReviews(campsiteId)
    }

    @Test
    fun delete_activeReservation_blocksDeletion() = runBlocking<Unit> {
        doReturn(listOf(Reservation(id = "reservation-1", campsiteId = campsiteId)))
            .`when`(reservationRepository).listActiveForCampsite(campsiteId)

        val result = service.delete(campsiteId)

        assertEquals(CampsiteDeletionResult.BlockedByReservation, result)
        verify(campsiteRepository, never()).deleteWithReviews(campsiteId)
    }

    @Test
    fun delete_lotteryQueryFails_propagatesErrorAndDoesNotDelete() = runBlocking<Unit> {
        val failure = IllegalStateException("lottery query failed")
        doThrow(failure).`when`(lotteryRepository).listByCampsite(campsiteId)

        try {
            service.delete(campsiteId)
            fail("Expected lottery query failure")
        } catch (actual: IllegalStateException) {
            assertSame(failure, actual)
        }

        verify(reservationRepository, never()).listActiveForCampsite(campsiteId)
        verify(campsiteRepository, never()).deleteWithReviews(campsiteId)
    }

    @Test
    fun delete_reservationQueryFails_propagatesErrorAndDoesNotDelete() = runBlocking<Unit> {
        val failure = IllegalStateException("reservation query failed")
        doThrow(failure).`when`(reservationRepository)
            .listActiveForCampsite(campsiteId)

        try {
            service.delete(campsiteId)
            fail("Expected reservation query failure")
        } catch (actual: IllegalStateException) {
            assertSame(failure, actual)
        }

        verify(campsiteRepository, never()).deleteWithReviews(campsiteId)
    }

    @Test
    fun delete_campsiteDeletionFails_propagatesError() = runBlocking<Unit> {
        val failure = IllegalStateException("delete failed")
        doThrow(failure).`when`(campsiteRepository).deleteWithReviews(campsiteId)

        try {
            service.delete(campsiteId)
            fail("Expected campsite deletion failure")
        } catch (actual: IllegalStateException) {
            assertSame(failure, actual)
        }
    }

    @Test
    fun delete_blankId_rejectsRequestWithoutAccessingRepositories() = runBlocking<Unit> {
        try {
            service.delete("  ")
            fail("Expected invalid campsite ID")
        } catch (actual: IllegalArgumentException) {
            assertEquals("Campsite ID cannot be blank", actual.message)
        }

        verify(lotteryRepository, never()).listByCampsite("  ")
        verify(reservationRepository, never()).listActiveForCampsite("  ")
        verify(campsiteRepository, never()).deleteWithReviews("  ")
    }

    private fun lottery(
        status: String,
        start: Timestamp? = null,
        end: Timestamp? = null
    ) = Lottery(
        campsiteID = campsiteId,
        status = status,
        bookingStart = start,
        bookingEnd = end
    )
}
