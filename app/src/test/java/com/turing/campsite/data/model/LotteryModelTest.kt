package com.turing.campsite.data.model

import com.google.firebase.Timestamp
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Checks lottery defaults, configuration retention, and registration-window boundaries.
 *
 * Exercises only model behavior; no persistence, UI, or server authorization is tested.
 */
class LotteryModelTest {
    private val fixed = Timestamp(1_000L, 123)
    private val later = Timestamp(2_000L, 456)

    private fun subject() = Lottery(registrationStart = fixed, registrationEnd = later, status = "open")

    /** Verifies empty configuration, closed status, and no waitlist limit, with registration closed when dates are absent. */
    @Test fun defaultsAreClosedUnlimitedAndUndated() {
        val lottery = Lottery()
        assertEquals("", lottery.id)
        assertEquals("", lottery.owner)
        assertEquals("", lottery.campsiteID)
        assertNull(lottery.registrationStart)
        assertNull(lottery.registrationEnd)
        assertNull(lottery.bookingStart)
        assertNull(lottery.bookingEnd)
        assertEquals(0, lottery.winnerCount)
        assertEquals(0L, lottery.waitlistCap) // 0 means no limit
        assertEquals("closed", lottery.status)
        assertFalse(lottery.isRegistrationOpen(fixed))
    }

    /** Verifies that registration includes both the start and end timestamps. */
    @Test fun bothRegistrationEndpointsAreInclusive() {
        assertTrue(subject().isRegistrationOpen(fixed))
        assertTrue(subject().isRegistrationOpen(later))
    }

    /** Verifies that registration is closed one nanosecond before the start and after the end. */
    @Test fun immediatelyOutsideRegistrationEndpointsIsClosed() {
        assertFalse(subject().isRegistrationOpen(Timestamp(1_000L, 122)))
        assertFalse(subject().isRegistrationOpen(Timestamp(2_000L, 457)))
    }

    /** Verifies that registration is open one nanosecond inside each endpoint. */
    @Test fun immediatelyInsideRegistrationEndpointsIsOpen() {
        assertTrue(subject().isRegistrationOpen(Timestamp(1_000L, 124)))
        assertTrue(subject().isRegistrationOpen(Timestamp(2_000L, 455)))
    }

    /** Verifies timestamp ordering across a second boundary while retaining nanosecond precision. */
    @Test fun secondsTakePrecedenceOverNanoseconds() {
        val lottery = subject().copy(registrationStart = Timestamp(1_000L, 999_999_999), registrationEnd = Timestamp(1_001L, 1))
        assertFalse(lottery.isRegistrationOpen(Timestamp(1_000L, 999_999_998)))
        assertTrue(lottery.isRegistrationOpen(Timestamp(1_001L, 0)))
        assertFalse(lottery.isRegistrationOpen(Timestamp(1_001L, 2)))
    }

    /** Verifies that either missing registration endpoint, or both missing, closes registration. */
    @Test fun eachMissingEndpointClosesRegistration() {
        assertFalse(subject().copy(registrationStart = null).isRegistrationOpen(fixed))
        assertFalse(subject().copy(registrationEnd = null).isRegistrationOpen(fixed))
        assertFalse(subject().copy(registrationStart = null, registrationEnd = null).isRegistrationOpen(fixed))
    }

    /** Verifies that DRAWN and CLOSED statuses reject the start, middle, and end of the registration window. */
    @Test fun allNonOpenStatusesCloseEntireRegistrationWindow() {
        for (status in listOf("drawn", "closed")) {
            for (time in listOf(fixed, Timestamp(1_500L, 0), later)) {
                assertFalse("$status at $time", subject().copy(status = status).isRegistrationOpen(time))
            }
        }
    }

    /** Verifies that equal registration endpoints admit only their shared instant and reject adjacent nanoseconds. */
    @Test fun equalEndpointsAdmitExactlyOneInstant() {
        val lottery = subject().copy(registrationEnd = fixed)
        assertFalse(lottery.isRegistrationOpen(Timestamp(1_000L, 122)))
        assertTrue(lottery.isRegistrationOpen(fixed))
        assertFalse(lottery.isRegistrationOpen(Timestamp(1_000L, 124)))
    }

    /** Verifies that a reversed registration window rejects the sampled instants before, within, and after its endpoints. */
    @Test fun reversedRegistrationWindowIsNeverOpenAtSampledInstants() {
        val lottery = subject().copy(registrationStart = later, registrationEnd = fixed)
        for (time in listOf(Timestamp(0L, 0), fixed, Timestamp(1_500L, 0), later, Timestamp(3_000L, 0))) {
            assertFalse(lottery.isRegistrationOpen(time))
        }
    }

    /** Verifies that status and date mutations immediately affect the registration predicate. */
    @Test fun changingStatusAndDatesChangesRegistrationResult() {
        val lottery = subject()
        lottery.status = "closed"
        assertFalse(lottery.isRegistrationOpen(fixed))
        lottery.status = "open"
        assertTrue(lottery.isRegistrationOpen(fixed))
        lottery.registrationStart = later
        assertFalse(lottery.isRegistrationOpen(fixed))
        lottery.registrationEnd = later
        assertTrue(lottery.isRegistrationOpen(later))
    }

    /** Verifies that booking dates neither exclude an in-window registration time nor admit an out-of-window time. */
    @Test fun bookingWindowDoesNotDetermineRegistrationEligibility() {
        val lottery = subject().copy(bookingStart = Timestamp(3_000L, 0), bookingEnd = Timestamp(4_000L, 0))
        assertTrue(lottery.isRegistrationOpen(fixed))
        assertFalse(lottery.isRegistrationOpen(Timestamp(3_500L, 0)))
    }

    /** Verifies that zero winners and a zero or one waitlist capacity do not close an otherwise open window. */
    @Test fun registrationPredicateDoesNotCheckWinnerCountOrWaitlistCapacity() {
        // Capacity enforcement is not implemented in this model predicate.
        for (capacity in listOf(0L, 1L)) {
            val lottery = subject().copy(waitlistCap = capacity, winnerCount = 0)
            assertTrue(lottery.isRegistrationOpen(fixed))
        }
    }

    /** Verifies that copying a lottery with a selected site and draw configuration retains those values. */
    @Test fun selectedSiteAndDrawConfigurationAreRetained() {
        val lottery = subject().copy(id = "lottery-1", owner = "owner-1", campsiteID = "site-1", winnerCount = 2, waitlistCap = 10)
        assertEquals("lottery-1", lottery.id)
        assertEquals("owner-1", lottery.owner)
        assertEquals("site-1", lottery.campsiteID)
        assertEquals(2, lottery.winnerCount)
        assertEquals(10L, lottery.waitlistCap)
    }

    // Tests retained from LotteryTest, including its per-test fixture setup.


    private lateinit var lottery: Lottery

    // Registration runs from second 1000 to second 2000
    private val start = Timestamp(1_000L, 0)
    private val end = Timestamp(2_000L, 0)

    // Set up a fresh open lottery before every single test
    @Before
    fun setUp() {
        lottery = Lottery(
            id = "lottery1",
            registrationStart = start,
            registrationEnd = end,
            status = "open"
        )
    }

    @Test
    fun isRegistrationOpen_duringPeriod_true() {
        val open = lottery.isRegistrationOpen(Timestamp(1_500L, 0)) // halfway through

        assertTrue(open)
    }

    @Test
    fun isRegistrationOpen_onBoundaries_true() {
        assertTrue(lottery.isRegistrationOpen(start)) // exactly at the start
        assertTrue(lottery.isRegistrationOpen(end))   // exactly at the end
    }

    @Test
    fun isRegistrationOpen_beforeStart_false() {
        val open = lottery.isRegistrationOpen(Timestamp(999L, 0)) // one second early

        assertFalse(open)
    }

    @Test
    fun isRegistrationOpen_afterEnd_false() {
        val open = lottery.isRegistrationOpen(Timestamp(2_001L, 0)) // one second late

        assertFalse(open)
    }

    @Test
    fun isRegistrationOpen_statusDrawn_false() {
        lottery.status = "drawn" // winners already picked
        val open = lottery.isRegistrationOpen(Timestamp(1_500L, 0))

        assertFalse(open)
    }

    @Test
    fun isRegistrationOpen_missingDates_false() {
        lottery.registrationStart = null
        lottery.registrationEnd = null
        val open = lottery.isRegistrationOpen(Timestamp(1_500L, 0))

        assertFalse(open)
    }

    @Test
    fun isRegistrationOpen_missingStart_false() {
        lottery.registrationStart = null
        assertFalse(lottery.isRegistrationOpen(Timestamp(1_500L, 0)))
    }

    @Test
    fun isRegistrationOpen_missingEnd_false() {
        lottery.registrationEnd = null
        assertFalse(lottery.isRegistrationOpen(Timestamp(1_500L, 0)))
    }

    @Test
    fun isRegistrationOpen_statusClosed_false() {
        lottery.status = "closed"
        assertFalse(lottery.isRegistrationOpen(Timestamp(1_500L, 0)))
    }

    @Test
    fun isRegistrationOpen_startNanosecondBoundary_isInclusive() {
        lottery.registrationStart = Timestamp(1_000L, 500_000_000)
        assertFalse(lottery.isRegistrationOpen(Timestamp(1_000L, 499_999_999)))
        assertTrue(lottery.isRegistrationOpen(Timestamp(1_000L, 500_000_000)))
        assertTrue(lottery.isRegistrationOpen(Timestamp(1_000L, 500_000_001)))
    }

    @Test
    fun isRegistrationOpen_endNanosecondBoundary_isInclusive() {
        lottery.registrationEnd = Timestamp(2_000L, 500_000_000)
        assertTrue(lottery.isRegistrationOpen(Timestamp(2_000L, 499_999_999)))
        assertTrue(lottery.isRegistrationOpen(Timestamp(2_000L, 500_000_000)))
        assertFalse(lottery.isRegistrationOpen(Timestamp(2_000L, 500_000_001)))
    }
}
