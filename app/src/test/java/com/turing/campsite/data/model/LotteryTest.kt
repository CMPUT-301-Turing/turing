package com.turing.campsite.data.model

import com.google.firebase.Timestamp
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LotteryTest {

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
            status = LotteryStatus.OPEN
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
        lottery.status = LotteryStatus.DRAWN // winners already picked
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
}
