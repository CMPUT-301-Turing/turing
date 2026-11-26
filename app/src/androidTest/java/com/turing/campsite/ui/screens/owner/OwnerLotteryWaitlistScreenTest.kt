/* Checks waitlist capacity and empty state with a fixture lottery; it does not read Firestore. */
package com.turing.campsite.ui.screens.owner

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.turing.campsite.data.model.Lottery
import org.junit.Rule
import org.junit.Test

class OwnerLotteryWaitlistScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun selectedLotteryShowsActualCapAndEmptyWaitlist() {
        compose.setContent {
            OwnerLotteryDetailsScreen({}, {}, Lottery(id = "selected-lot", waitlistCap = 3))
        }
        compose.onNodeWithText("0 / 3").assertIsDisplayed()
        compose.onNodeWithText("No entrants have joined.").assertIsDisplayed()
    }
}
