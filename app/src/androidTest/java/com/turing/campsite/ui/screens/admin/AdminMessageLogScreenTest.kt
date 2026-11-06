package com.turing.campsite.ui.screens.admin

import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue


/**
 * AdminMessageLogScreen UI test
 *
 * Testing the functionality of buttons and the hardcoded data filters...
 */
class AdminMessageLogScreenTest {

    // ...
    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Test if screen show the important text from sample data
     */
    @Test
    fun adminMessageLogScreen_displaysCorrectText() {
        composeTestRule.setContent {

            AdminMessageLogScreen(
                onBackClick = {},

                onConversationClick = {}
            )
        }

        // check header and search bar
        composeTestRule.onNodeWithText("Message Logs").assertIsDisplayed()

        composeTestRule.onNodeWithText("Search sender or receiver").assertIsDisplayed()



        // check sample data visible

        // every card shows the owner line, so look at the first one
        composeTestRule.onAllNodesWithText("Owner: John Smith →").onFirst().assertIsDisplayed()

        composeTestRule.onAllNodesWithText("Entrant: Jane Doe").onFirst().assertIsDisplayed()

        // the newest message text
        composeTestRule.onNodeWithText("Perfect, see you at 3 PM tomorrow! Thanks for your help.").assertIsDisplayed()
    }



    /**
     * Tests if back button triggers the callback correctly
     */
    @Test
    fun adminMessageLogScreen_backButtonClicks() {

        var backWasClicked = false

        composeTestRule.setContent {

            AdminMessageLogScreen(
                onBackClick = { backWasClicked = true },
                onConversationClick = {}
            )
        }

        // Click back arrow
        composeTestRule.onNodeWithContentDescription("Back to admin home").performClick()

        assertTrue(backWasClicked)
    }



    /**
     * Tests if clicking a message box trigger onConversationClick with correct ID
     */
    @Test
    fun adminMessageLogScreen_boxClickPassesCorrectId() {

        var clickedId = ""

        composeTestRule.setContent {
            AdminMessageLogScreen(
                onBackClick = {},

                onConversationClick = { id -> clickedId = id }
            )
        }

        // click first owner
        composeTestRule.onNodeWithText("Perfect, see you at 3 PM tomorrow! Thanks for your help.").performClick()

        // check if it passed the message
        assertEquals("message-1", clickedId)
    }

    /**
     * Test that the filter buttons can be clicked and the list stays on screen.
     * Messages have no reservation status yet, so the filters do not hide anything.
     */
    @Test
    fun adminMessageLogScreen_filterButtonsCanBeClicked() {

        composeTestRule.setContent {
            AdminMessageLogScreen(
                onBackClick = {},
                onConversationClick = {}
            )
        }

        // click each filter button
        composeTestRule.onNodeWithText("Active Reservations").performClick()
        composeTestRule.onNodeWithText("Inactive Reservations").performClick()
        composeTestRule.onNodeWithText("All").performClick()

        // the newest message is still there
        composeTestRule.onNodeWithText("Perfect, see you at 3 PM tomorrow! Thanks for your help.").assertIsDisplayed()
    }
}