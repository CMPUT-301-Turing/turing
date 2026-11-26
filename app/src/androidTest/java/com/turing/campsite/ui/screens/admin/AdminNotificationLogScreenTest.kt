/* Exercises notification rendering and the load-error state with fixture data. */
package com.turing.campsite.ui.screens.admin

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.turing.campsite.data.model.Notification
import com.turing.campsite.data.model.NotificationType
import org.junit.Rule
import org.junit.Test

class AdminNotificationLogScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun showsSuppliedNotificationAndRecipient() {
        compose.setContent {
            AdminNotificationLogScreen(onBackClick = {}, notifications = listOf(
                Notification(id = "n1", toUserId = "entrant-a",
                    body = "Your reservation is confirmed.",
                    type = NotificationType.RESERVATION_CONFIRMED)
            ))
        }
        compose.onNodeWithText("To: entrant-a").assertIsDisplayed()
        compose.onNodeWithText("Your reservation is confirmed.").assertIsDisplayed()
    }

    @Test
    fun showsExplicitLoadErrorRatherThanFakeRecords() {
        compose.setContent { AdminNotificationLogScreen({}, status = "Could not load records.") }
        compose.onNodeWithText("Could not load records.").assertIsDisplayed()
        compose.onNodeWithText("To: entrant-a").assertDoesNotExist()
    }
}
