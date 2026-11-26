/* Checks ownership, effective booking dates, and tab assignment with a fixed calendar date. */
package com.turing.campsite.ui.screens.owner

import com.google.firebase.Timestamp
import com.turing.campsite.data.model.Reservation
import com.turing.campsite.data.model.ReservationStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date

/**
 * Tests owner and status filters without a Firestore connection.
 */
class OwnerReservationFiltersTest {
    @Test
    fun ownerQueryResultsShowBothOwnedCampsitesAndExcludeUnexpectedOwner() {
        val pinewood = booking("pinewood", "owner-a", "campsite-1")
        val riverbend = booking("riverbend", "owner-a", "campsite-2")
        val otherOwner = booking("oakridge", "owner-b", "campsite-3")

        val visible = OwnerReservationTab.ACTIVE.select(
            listOf(pinewood, otherOwner, riverbend).ownedBy("owner-a")
        )

        assertEquals(listOf(pinewood, riverbend), visible)
    }

    @Test
    fun eachStatusAppearsOnlyInTheMatchingTab() {
        val active = booking("active", "owner-a", "campsite-1")
        val completed = active.copy(id = "completed", status = ReservationStatus.COMPLETED)
        val cancelled = active.copy(id = "cancelled", status = ReservationStatus.CANCELLED)
        val rejected = active.copy(id = "rejected", status = ReservationStatus.REJECTED)
        val all = listOf(active, completed, cancelled, rejected)

        assertEquals(listOf(active), OwnerReservationTab.ACTIVE.select(all))
        assertEquals(listOf(completed), OwnerReservationTab.PAST.select(all))
        assertEquals(listOf(cancelled, rejected), OwnerReservationTab.CANCELLED.select(all))
        assertTrue(OwnerReservationTab.PAST.select(listOf(active)).isEmpty())
    }

    @Test
    fun noSignedInOwnerSeesNoReservations() {
        val reservations = listOf(booking("missing-owner", "", "campsite-1"))

        assertTrue(reservations.ownedBy(null).isEmpty())
        assertTrue(reservations.ownedBy("").isEmpty())
    }

    @Test
    fun activeRecordWithAnElapsedLastNightAppearsInPastEvenWithoutStatusUpdate() {
        val dayStart = Date(20_000)
        val ended = booking("ended", "owner-a", "campsite-1")
            .copy(endDate = Timestamp(Date(19_999)))

        assertEquals(listOf(ended), OwnerReservationTab.PAST.select(listOf(ended), dayStart))
        assertTrue(OwnerReservationTab.ACTIVE.select(listOf(ended), dayStart).isEmpty())
    }

    @Test
    fun lastNightTodayOrLaterDoesNotEndTheBookingEarly() {
        val dayStart = Date(20_000)
        val sameDay = booking("today", "owner-a", "campsite-1")
            .copy(endDate = Timestamp(dayStart))
        val later = sameDay.copy(id = "later", endDate = Timestamp(Date(50_000)))

        assertEquals(listOf(sameDay, later), OwnerReservationTab.ACTIVE.select(listOf(sameDay, later), dayStart))
    }

    @Test
    fun elapsedCancelledAndRejectedBookingsStayCancelled() {
        val dayStart = Date(20_000)
        val cancelled = booking("cancelled", "owner-a", "campsite-1")
            .copy(endDate = Timestamp(Date(0)), status = ReservationStatus.CANCELLED)
        val rejected = cancelled.copy(id = "rejected", status = ReservationStatus.REJECTED)
        val bookings = listOf(cancelled, rejected)

        assertEquals(bookings, OwnerReservationTab.CANCELLED.select(bookings, dayStart))
        assertTrue(OwnerReservationTab.PAST.select(bookings, dayStart).isEmpty())
    }

    private fun booking(id: String, ownerId: String, campsiteId: String) =
        Reservation(id = id, ownerId = ownerId, campsiteId = campsiteId)
}
