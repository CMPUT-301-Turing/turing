/* AI Assistance Disclosure: OpenAI, ChatGPT (GPT-6), 2026-11-26.
 * Scope: Four campsite cascade coordination tests. Firebase permissions are not mocked proof.
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
    private val lottery = Lottery(id = "lottery", campsiteID = "site", ownerId = "owner")
    private val reservation = Reservation(id = "reservation", campsiteId = "site", entrantId = "entrant")
    private lateinit var service: CampsiteDeletionService

    @Before fun setUp() = runBlocking<Unit> {
        service = CampsiteDeletionService(campsites, lotteries, reservations, messages, users, notifier) { "owner" }
        doReturn(User(id = "owner")).`when`(users).get("owner")
        doReturn(campsite).`when`(campsites).get("site")
        doReturn(null).`when`(campsites).beginDeletion("site")
        doReturn(listOf(lottery)).`when`(lotteries).listByCampsiteForDeletion("site")
        doReturn(listOf(LotteryEntry(entrantId = "entrant"))).`when`(lotteries).listEntriesForDeletion("lottery")
        doReturn(listOf(reservation)).`when`(reservations).listByCampsite("site")
        doReturn(listOf(reservation)).`when`(reservations).listByLottery("lottery")
        doReturn(listOf("owner", "entrant")).`when`(campsites)
            .captureDeletionRecipients("site", listOf("owner", "entrant"))
    }

    @Test fun cascade_checkpointsBeforeDeletingAndRemovesChildrenBeforeParents() = runBlocking<Unit> {
        // The administrator can remove someone else's campsite, including active dependencies.
        doReturn(User(id = "owner", roles = listOf(UserRole.ADMIN))).`when`(users).get("owner")
        campsite.ownerId = "different-owner"
        doReturn(listOf("different-owner", "entrant")).`when`(campsites)
            .captureDeletionRecipients("site", listOf("different-owner", "entrant"))
        assertEquals(CampsiteDeletionResult.Deleted, service.delete("site"))
        val ordered = inOrder(campsites, reservations, messages, lotteries, notifier)
        ordered.verify(campsites).beginDeletion("site")
        ordered.verify(campsites).captureDeletionRecipients("site", listOf("different-owner", "entrant"))
        ordered.verify(messages).deleteForReservation("reservation")
        ordered.verify(reservations).delete("reservation")
        ordered.verify(lotteries).deleteEntries("lottery")
        ordered.verify(lotteries).delete("lottery")
        ordered.verify(notifier).notify(campsite, listOf("different-owner", "entrant"), "owner")
        ordered.verify(campsites).deleteReviews("site")
        ordered.verify(campsites).delete("site")
        verify(reservations, times(1)).delete("reservation")
    }

    @Test fun recipients_deduplicatesOwnerWaitlistAndBookingsAcrossLotteries() = runBlocking<Unit> {
        doReturn(listOf(lottery, lottery.copy(id = "lottery2"))).`when`(lotteries).listByCampsiteForDeletion("site")
        doReturn(listOf(LotteryEntry(entrantId = "owner"), LotteryEntry(entrantId = "entrant")))
            .`when`(lotteries).listEntriesForDeletion("lottery2")
        doReturn(emptyList<Reservation>()).`when`(reservations).listByLottery("lottery2")
        service.delete("site")
        verify(campsites).captureDeletionRecipients("site", listOf("owner", "entrant"))
        verify(notifier).notify(campsite, listOf("owner", "entrant"), "owner")
        verify(lotteries).delete("lottery2")
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

    @Test fun interruptedCleanup_keepsParentAndRetryUsesSavedRecipients() = runBlocking<Unit> {
        doThrow(IllegalStateException("connection lost")).doReturn(Unit).`when`(notifier)
            .notify(campsite, listOf("owner", "entrant"), "owner")
        try {
            service.delete("site")
            fail("Expected partial-failure report")
        } catch (failure: IllegalStateException) {
            assertTrue(failure.message!!.contains("retry deletion"))
            assertEquals("connection lost", failure.cause!!.message)
        }
        // Dependencies were removed, but the parent preserves the recipients for retry.
        verify(reservations).delete("reservation")
        verify(lotteries).delete("lottery")
        verify(campsites, never()).delete("site")
        doReturn(listOf("owner", "entrant")).`when`(campsites).beginDeletion("site")
        doReturn(emptyList<Lottery>()).`when`(lotteries).listByCampsiteForDeletion("site")
        doReturn(emptyList<Reservation>()).`when`(reservations).listByCampsite("site")
        assertEquals(CampsiteDeletionResult.Deleted, service.delete("site"))
        verify(lotteries, times(1)).listEntriesForDeletion("lottery")
        verify(campsites, times(1)).captureDeletionRecipients("site", listOf("owner", "entrant"))
        // The notifier must resume its own atomic delivery checkpoints on the second call.
        verify(notifier, times(2)).notify(campsite, listOf("owner", "entrant"), "owner")
        verify(campsites).delete("site")
    }
}
