/* Checks direct campsite actions and lottery states without a Firebase session. */
package com.turing.campsite.ui.screens.owner

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.turing.campsite.data.model.Campsite
import com.turing.campsite.data.model.Lottery
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class OwnerCampsiteActionsTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun campsiteHasDirectActionsAboveCalendar() {
        var reservations = false
        var lottery = false
        compose.setContent {
            OwnerCampsiteFullDetailScreen(
                campsite = Campsite(id = "site", name = "River Camp"),
                onBackClick = {},
                onEditClick = {},
                onQRCodeClick = {},
                onLotteryClick = { lottery = true },
                onReservationsClick = { reservations = true }
            )
        }
        compose.onNodeWithText("Reservations").assertIsDisplayed().performClick()
        compose.onNodeWithText("View Waitlist").assertIsDisplayed().performClick()
        compose.runOnIdle {
            assertTrue(reservations)
            assertTrue(lottery)
        }
    }

    @Test
    fun noLinkedLotteryShowsClearReadOnlyState() {
        compose.setContent {
            OwnerLotteryDetailsScreen(
                onBackClick = {}, readOnly = true, campsiteName = "River Camp",
                statusMessage = "No lottery has been created for this campsite."
            )
        }
        compose.onNodeWithText("River Camp").assertIsDisplayed()
        compose.onNodeWithText("No lottery has been created for this campsite.").assertIsDisplayed()
        compose.onNodeWithText("Edit Lottery").assertDoesNotExist()
    }

    @Test
    fun linkedLotteryShowsStatusWithoutEditing() {
        compose.setContent {
            OwnerLotteryDetailsScreen(
                onBackClick = {}, readOnly = true, campsiteName = "River Camp",
                lottery = Lottery(id = "lot", status = "drawn", winnerCount = 4)
            )
        }
        compose.onNodeWithText("Lottery status: Drawn").assertIsDisplayed()
        compose.onNodeWithText("Winners to draw: 4").assertIsDisplayed()
        compose.onNodeWithText("Edit Lottery").assertDoesNotExist()
        compose.onNodeWithText("Create Lottery").assertDoesNotExist()
    }

    @Test
    fun signedOutOwnerHomeShowsReasonAndDisablesRegistration() {
        compose.setContent {
            OwnerHomeScreen(
                campsites = emptyList(),
                onNotificationsClick = {},
                onProfileClick = {},
                onRegisterCampsiteClick = {},
                onCreateLotteryClick = {},
                onCampsiteClick = {},
                canManageCampsites = false,
                campsiteStatus = "Sign in before managing campsites."
            )
        }
        compose.onNodeWithText("Sign in before managing campsites.").assertIsDisplayed()
        compose.onNodeWithText("+ Register New Campsite").assertIsNotEnabled()
        compose.onNodeWithText("Create Lottery").assertIsNotEnabled()
    }

}
