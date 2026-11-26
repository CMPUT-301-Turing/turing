/* Checks reservation tabs and confirmation behavior with fixture bookings. */
package com.turing.campsite.ui.screens.owner

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.firebase.Timestamp
import com.turing.campsite.data.model.Reservation
import com.turing.campsite.data.model.ReservationStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.util.Date

/**
 * Verifies the owner history screen using explicit, in-memory reservation fixtures.
 */
class OwnerCampsiteHistoryScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun booking(
        id: String,
        campsiteId: String,
        status: ReservationStatus = ReservationStatus.ACTIVE
    ) = Reservation(id = id, entrantId = "Entrant $id", ownerId = "owner-1", campsiteId = campsiteId,
        status = status)

    @Test
    fun backButtonCallsCallback() {
        var backClicked = false
        composeTestRule.setContent {
            OwnerCampsiteHistoryScreen(
                onBackClick = { backClicked = true },
                onMessageClick = {},
                onRejectClick = {},
                reservations = emptyList()
            )
        }

        composeTestRule.onNodeWithContentDescription("Back to campsite details").performClick()
        composeTestRule.runOnIdle { assertTrue(backClicked) }
    }

    @Test
    fun tabsShowOnlyMatchingBookingsAcrossOwnedCampsites() {
        val first = booking("active-a", "Pinewood")
        val second = booking("active-b", "Riverbend")
        val cancelled = booking("cancelled", "Pinewood", ReservationStatus.CANCELLED)
        val rejected = booking("rejected", "Riverbend", ReservationStatus.REJECTED)
        val completed = booking("completed", "Pinewood", ReservationStatus.COMPLETED)
        val reservations = listOf(first, second, cancelled, rejected, completed)
        composeTestRule.setContent {
            OwnerCampsiteHistoryScreen(
                onBackClick = {},
                onMessageClick = {},
                onRejectClick = {},
                reservations = reservations
            )
        }

        composeTestRule.onNodeWithText("2 bookings").assertIsDisplayed()
        composeTestRule.onNodeWithText(first.entrantId).assertIsDisplayed()
        composeTestRule.onNodeWithText(second.entrantId).assertIsDisplayed()
        composeTestRule.onNodeWithText(completed.entrantId).assertDoesNotExist()

        composeTestRule.onNodeWithText("Past").performClick()
        composeTestRule.onNodeWithText(completed.entrantId).assertIsDisplayed()
        composeTestRule.onNodeWithText(first.entrantId).assertDoesNotExist()
        composeTestRule.onNodeWithText("Reject").assertDoesNotExist()

        composeTestRule.onNodeWithText("Cancelled").performClick()
        composeTestRule.onNodeWithText("2 bookings").assertIsDisplayed()
        composeTestRule.onNodeWithText(cancelled.entrantId).assertIsDisplayed()
        composeTestRule.onNodeWithText(rejected.entrantId).assertIsDisplayed()
        composeTestRule.onNodeWithText(completed.entrantId).assertDoesNotExist()
    }

    @Test
    fun pastBookingKeepsStoredReferenceWithoutCampsiteData() {
        val archived = booking("archived", "missing-site-id", ReservationStatus.COMPLETED)
        composeTestRule.setContent {
            OwnerCampsiteHistoryScreen(
                onBackClick = {},
                onMessageClick = {},
                onRejectClick = {},
                reservations = listOf(archived)
            )
        }

        composeTestRule.onNodeWithText("Past").performClick()
        composeTestRule.onNodeWithText(archived.entrantId).assertIsDisplayed()
        composeTestRule.onNodeWithText("Campsite ref: missing-site-id").assertIsDisplayed()
    }

    @Test
    fun elapsedActiveBookingShowsAsCompletedAndCannotBeRejected() {
        val elapsed = booking("elapsed", "Pinewood").copy(endDate = Timestamp(Date(0)))
        composeTestRule.setContent {
            OwnerCampsiteHistoryScreen(
                onBackClick = {},
                onMessageClick = {},
                onRejectClick = { error("Past bookings cannot be rejected") },
                reservations = listOf(elapsed)
            )
        }

        composeTestRule.onNodeWithText(elapsed.entrantId).assertDoesNotExist()
        composeTestRule.onNodeWithText("Past").performClick()
        composeTestRule.onNodeWithText(elapsed.entrantId).assertIsDisplayed()
        composeTestRule.onNodeWithText("Completed").assertIsDisplayed()
        composeTestRule.onNodeWithText("Reject").assertDoesNotExist()
    }

    @Test
    fun emptyTabShowsEmptyState() {
        composeTestRule.setContent {
            OwnerCampsiteHistoryScreen(
                onBackClick = {},
                onMessageClick = {},
                onRejectClick = {},
                reservations = listOf(booking("active-a", "Pinewood"))
            )
        }

        composeTestRule.onNodeWithText("Past").performClick()
        composeTestRule.onNodeWithText("0 bookings").assertIsDisplayed()
        composeTestRule.onNodeWithText("No bookings in this tab.").assertIsDisplayed()
    }

    @Test
    fun rejectButtonRequiresConfirmationAndUpdatesShownTab() {
        val reservation = booking("active-a", "Pinewood")
        val reservations = mutableStateOf(listOf(reservation))
        var rejectedId: String? = null
        composeTestRule.setContent {
            OwnerCampsiteHistoryScreen(
                onBackClick = {},
                onMessageClick = {},
                onRejectClick = { id ->
                    rejectedId = id
                    reservations.value = listOf(reservation.copy(status = ReservationStatus.REJECTED))
                },
                reservations = reservations.value
            )
        }

        composeTestRule.onNodeWithText("Reject").performClick()
        composeTestRule.onNodeWithText("Keep booking").performClick()
        composeTestRule.runOnIdle { assertNull(rejectedId) }

        composeTestRule.onNodeWithText("Reject").performClick()
        composeTestRule.onNodeWithText("Reject reservation").performClick()
        composeTestRule.runOnIdle { assertEquals(reservation.id, rejectedId) }
        composeTestRule.onNodeWithText(reservation.entrantId).assertDoesNotExist()
        composeTestRule.onNodeWithText("Cancelled").performClick()
        composeTestRule.onNodeWithText(reservation.entrantId).assertIsDisplayed()
    }
}
