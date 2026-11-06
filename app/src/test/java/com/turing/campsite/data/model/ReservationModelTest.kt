package com.turing.campsite.data.model

import com.google.firebase.Timestamp
import org.junit.Assert.*
import org.junit.Test

/**
 * Checks reservation defaults, relationships, dates, and independent status and rules fields.
 *
 * Exercises only model behavior; no persistence, UI, or server authorization is tested.
 */
class ReservationModelTest {
    private val fixed = Timestamp(1_000L, 123)
    private val later = Timestamp(2_000L, 456)

    /** Verifies empty identifiers, absent booking dates, ACTIVE status, unaccepted rules, and the supplied creation time. */
    @Test fun defaultsAreActiveUndatedAndRulesNotAccepted() {
        val subject = Reservation(createdAt = fixed)
        assertEquals("", subject.id)
        assertEquals("", subject.entrantId)
        assertEquals("", subject.ownerId)
        assertEquals("", subject.campsiteId)
        assertEquals("", subject.lotteryId)
        assertNull(subject.startDate)
        assertNull(subject.endDate)
        assertEquals(ReservationStatus.ACTIVE, subject.status)
        assertFalse(subject.rulesAccepted)
        assertEquals(fixed, subject.createdAt)
    }

    private fun booking() = Reservation(id = "reservation-1", entrantId = "entrant-1",
        ownerId = "owner-1", campsiteId = "site-1", lotteryId = "lottery-1",
        startDate = fixed, endDate = later, rulesAccepted = true, createdAt = fixed)

    /** Verifies that construction retains all supplied relationship identifiers, booking dates, and rules acceptance. */
    @Test fun constructorRetainsAllBookingRelationshipsAndDates() {
        val subject = booking()
        assertEquals("reservation-1", subject.id)
        assertEquals("entrant-1", subject.entrantId)
        assertEquals("owner-1", subject.ownerId)
        assertEquals("site-1", subject.campsiteId)
        assertEquals("lottery-1", subject.lotteryId)
        assertEquals(fixed, subject.startDate)
        assertEquals(later, subject.endDate)
        assertTrue(subject.rulesAccepted)
    }

    /** Verifies that copies using each reservation status retain all other booking fields. */
    @Test fun eachStatusPreservesBookingDetails() {
        val original = booking()
        for (status in ReservationStatus.values()) {
            val subject = original.copy(status = status)
            assertEquals(status, subject.status)
            assertEquals(original, subject.copy(status = original.status))
        }
    }

    /** Verifies that toggling rules acceptance does not change ACTIVE status. */
    @Test fun rulesAcceptanceIsAnExplicitFlagIndependentOfStatus() {
        val subject = booking().copy(rulesAccepted = false)
        assertFalse(subject.rulesAccepted)
        assertEquals(ReservationStatus.ACTIVE, subject.status)
        subject.rulesAccepted = true
        assertTrue(subject.rulesAccepted)
        assertEquals(ReservationStatus.ACTIVE, subject.status)
    }

    /** Verifies that either booking date can be cleared while the other remains unchanged. */
    @Test fun optionalDatesCanBeClearedIndependently() {
        val subject = booking()
        subject.startDate = null
        assertNull(subject.startDate)
        assertEquals(later, subject.endDate)
        subject.startDate = fixed
        subject.endDate = null
        assertEquals(fixed, subject.startDate)
        assertNull(subject.endDate)
    }

}
