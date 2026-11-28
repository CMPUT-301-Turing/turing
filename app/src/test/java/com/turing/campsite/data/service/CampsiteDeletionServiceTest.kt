/* AI Assistance Disclosure: OpenAI, ChatGPT (GPT-6), 2026-11-27.
 * Scope: Four cascade coordination cases adapted to merged shared-lottery schema.
 * Mocks verify coordination, not live Firestore rules or delivery transactions.
 */
package com.turing.campsite.data.service

import com.turing.campsite.data.model.*
import com.turing.campsite.data.repository.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class CampsiteDeletionServiceTest {
    private val campsites = mock(CampsiteRepository::class.java)
    private val lotteries = mock(LotteryRepository::class.java)
    private val reservations = mock(ReservationRepository::class.java)
    private val messages = mock(MessageRepository::class.java)
    private val users = mock(UserRepository::class.java)
    private val notifier = mock(CampsiteRemovalNotifier::class.java)
    private val campsite = Campsite(id = "site", ownerId = "owner", name = "Pinewood")
    private val lottery = CampsiteLotteryLink("lottery", listOf("site"))
    private val reservation = Reservation(id = "reservation", campsiteId = "site", entrantId = "entrant")
    private val plan = CampsiteDeletionPlan(listOf("owner", "entrant"), listOf("lottery"))
    private lateinit var service: CampsiteDeletionService

    @Before fun setUp() = runBlocking<Unit> {
        service = CampsiteDeletionService(campsites, lotteries, reservations, messages, users, notifier) { "owner" }
        doReturn(User(id = "owner")).`when`(users).get("owner")
        doReturn(campsite).`when`(campsites).get("site")
        doReturn(null).`when`(campsites).beginDeletion("site")
        doReturn(listOf(lottery)).`when`(lotteries).listByCampsiteForDeletion("site")
        doReturn(listOf("entrant")).`when`(lotteries).listParticipantIdsForRemoval("lottery")
        doReturn(listOf(reservation)).`when`(reservations).listByCampsite("site")
        doReturn(listOf(reservation)).`when`(reservations).listByLottery("lottery")
        doReturn(plan).`when`(campsites)
            .captureDeletionRecipients("site", plan.recipientIds, plan.lotteryIds)
        doReturn(CampsiteLotteryRemoval.DELETE_LOTTERY).`when`(lotteries).removeCampsite("lottery", "site")
    }

    @Test fun cascade_checkpointsBeforeDeletingAndRemovesChildrenBeforeParents() = runBlocking<Unit> {
        doReturn(User(id = "owner", roles = listOf(UserRole.ADMIN))).`when`(users).get("owner")
        campsite.ownerId = "different-owner"
        val adminPlan = plan.copy(recipientIds = listOf("different-owner", "entrant"))
        doReturn(adminPlan).`when`(campsites)
            .captureDeletionRecipients("site", adminPlan.recipientIds, adminPlan.lotteryIds)
        assertEquals(CampsiteDeletionResult.Deleted, service.delete("site"))
        val ordered = inOrder(campsites, reservations, messages, lotteries, notifier)
        ordered.verify(campsites).beginDeletion("site")
        ordered.verify(lotteries).beginCampsiteRemoval("lottery", "site")
        ordered.verify(campsites).captureDeletionRecipients("site", adminPlan.recipientIds, adminPlan.lotteryIds)
        ordered.verify(messages).deleteForReservation("reservation")
        ordered.verify(reservations).delete("reservation")
        ordered.verify(lotteries).removeCampsite("lottery", "site")
        ordered.verify(lotteries).deleteEntries("lottery")
        ordered.verify(lotteries).deleteForCampsiteRemoval("lottery", "site")
        ordered.verify(notifier).notify(campsite, adminPlan.recipientIds, "owner")
        ordered.verify(campsites).deleteReviews("site")
        ordered.verify(campsites).delete("site")
        verify(reservations, times(1)).delete("reservation")
    }

    @Test fun sharedLottery_preservesOtherBookingsAndNotifiesAllParticipantsOnce() = runBlocking<Unit> {
        val shared = lottery.copy(campsiteIds = listOf("site", "other-site"))
        val sole = CampsiteLotteryLink("lottery2", listOf("site"))
        val otherBooking = Reservation(id = "other-booking", campsiteId = "other-site", entrantId = "shared-entrant")
        val mixedPlan = CampsiteDeletionPlan(listOf("owner", "entrant", "shared-entrant"), listOf("lottery", "lottery2"))
        doReturn(listOf(shared, sole)).`when`(lotteries).listByCampsiteForDeletion("site")
        doReturn(listOf("owner", "entrant", "shared-entrant")).`when`(lotteries).listParticipantIdsForRemoval("lottery")
        doReturn(listOf("entrant")).`when`(lotteries).listParticipantIdsForRemoval("lottery2")
        doReturn(listOf(reservation, otherBooking)).`when`(reservations).listByLottery("lottery")
        doReturn(emptyList<Reservation>()).`when`(reservations).listByLottery("lottery2")
        doReturn(mixedPlan).`when`(campsites)
            .captureDeletionRecipients("site", mixedPlan.recipientIds, mixedPlan.lotteryIds)
        doReturn(CampsiteLotteryRemoval.RETAINED).`when`(lotteries).removeCampsite("lottery", "site")
        doReturn(CampsiteLotteryRemoval.DELETE_LOTTERY).`when`(lotteries).removeCampsite("lottery2", "site")
        assertEquals(CampsiteDeletionResult.Deleted, service.delete("site"))
        verify(notifier).notify(campsite, mixedPlan.recipientIds, "owner")
        verify(lotteries, never()).deleteEntries("lottery")
        verify(lotteries, never()).deleteForCampsiteRemoval("lottery", "site")
        verify(reservations, never()).delete("other-booking")
        verify(messages, never()).deleteForReservation("other-booking")
        verify(lotteries).deleteEntries("lottery2")
        verify(lotteries).deleteForCampsiteRemoval("lottery2", "site")
    }

    @Test fun unauthorizedActor_performsNoCleanupOrNotification() = runBlocking<Unit> {
        doReturn(User(id = "owner", roles = listOf(UserRole.ENTRANT))).`when`(users).get("owner")
        try {
            service.delete("site")
            fail("Expected authorization failure")
        } catch (_: SecurityException) { }
        verify(campsites, never()).beginDeletion("site")
        verify(campsites, never()).delete("site")
        verifyNoInteractions(lotteries, reservations, messages, notifier)
    }

    @Test fun interruptedDetach_retryUsesSavedLotteryIdsAndRecipients() = runBlocking<Unit> {
        doReturn(listOf(lottery.copy(campsiteIds = listOf("site", "other-site"))))
            .`when`(lotteries).listByCampsiteForDeletion("site")
        doReturn(CampsiteLotteryRemoval.RETAINED).doReturn(CampsiteLotteryRemoval.ALREADY_REMOVED)
            .`when`(lotteries).removeCampsite("lottery", "site")
        doThrow(IllegalStateException("connection lost")).doReturn(Unit).`when`(notifier)
            .notify(campsite, plan.recipientIds, "owner")
        try {
            service.delete("site")
            fail("Expected partial-failure report")
        } catch (failure: IllegalStateException) {
            assertTrue(failure.message!!.contains("retry deletion"))
            assertEquals("connection lost", failure.cause!!.message)
        }
        verify(reservations).delete("reservation")
        verify(campsites, never()).delete("site")
        doReturn(plan).`when`(campsites).beginDeletion("site")
        doReturn(emptyList<Reservation>()).`when`(reservations).listByCampsite("site")
        assertEquals(CampsiteDeletionResult.Deleted, service.delete("site"))
        verify(lotteries, times(1)).listByCampsiteForDeletion("site")
        verify(lotteries, times(1)).listParticipantIdsForRemoval("lottery")
        verify(campsites, times(1)).captureDeletionRecipients("site", plan.recipientIds, plan.lotteryIds)
        verify(lotteries, times(2)).removeCampsite("lottery", "site")
        verify(lotteries, never()).deleteEntries("lottery")
        verify(notifier, times(2)).notify(campsite, plan.recipientIds, "owner")
        verify(campsites).delete("site")
    }
}
