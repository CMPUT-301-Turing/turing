/* Exercises audit grouping, navigation, and the empty state with fixture messages. */
package com.turing.campsite.ui.screens.admin

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.turing.campsite.data.model.Message
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AdminMessageLogScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun groupsByReservationAndOpensReservationIdNotMessageId() {
        val fixtures = listOf(
            Message(id = "message-1", reservationId = "reservation-9",
                fromUserId = "owner-a", toUserId = "entrant-a", body = "Gate is open."),
            Message(id = "message-2", reservationId = "reservation-9",
                fromUserId = "owner-a", toUserId = "entrant-a", body = "Check in at noon.")
        )
        var opened: String? = null
        compose.setContent {
            AdminMessageLogScreen({}, { opened = it }, fixtures)
        }
        compose.onNodeWithText("1 conversation · Newest first").assertIsDisplayed()
        compose.onNodeWithText("Reservation: reservation-9").performClick()
        compose.runOnIdle { assertEquals("reservation-9", opened) }
    }

    @Test
    fun emptyAuditLogShowsEmptyState() {
        compose.setContent { AdminMessageLogScreen({}, {}) }
        compose.onNodeWithText("No owner-to-entrant conversations.").assertIsDisplayed()
    }
}
