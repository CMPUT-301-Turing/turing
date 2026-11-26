/* Exercises the administrator conversation UI with fixture reservation and participant IDs. */
package com.turing.campsite.ui.screens.admin

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.turing.campsite.data.model.Message
import org.junit.Rule
import org.junit.Test

class AdminConversationHistoryScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun usesActualReservationAndParticipantIds() {
        compose.setContent {
            AdminConversationHistoryScreen(listOf(
                Message(id = "m1", reservationId = "booking-b", fromUserId = "owner-a",
                    toUserId = "entrant-c", body = "Check in after 4.")
            ), {})
        }
        compose.onNodeWithText("Owner: owner-a").assertIsDisplayed()
        compose.onNodeWithText("Entrant: entrant-c").assertIsDisplayed()
        compose.onNodeWithText("Reservation: booking-b").assertIsDisplayed()
    }
}
