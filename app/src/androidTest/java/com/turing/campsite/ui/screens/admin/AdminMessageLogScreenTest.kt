package com.turing.campsite.ui.screens.admin

import org.junit.Rule
import org.junit.Test
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

        composeTestRule.onNodeWithText("Owner: John Smith →").assertIsDisplayed()

        composeTestRule.onNodeWithText("Entrant: Jane Doe").assertIsDisplayed()
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
        composeTestRule.onNodeWithText("Owner: John Smith →").performClick()

        // check if it passed the message
        assertEquals("message-1", clickedId)
    }

    /**
     * Test if filter button works and hides inactive reservations
     */
    @Test
    fun adminMessageLogScreen_filterButtonsWorks() {

        composeTestRule.setContent {

            AdminMessageLogScreen(

                onBackClick = {},
                onConversationClick = {}
            )
        }

        // initial visible is Ava Nguyen and is inactive
        composeTestRule.onNodeWithText("Owner: Ava Nguyen →").assertIsDisplayed()


        // click active reservations filter button
        composeTestRule.onNodeWithText("Active Reservations").performClick()



        // Ava Nguyen should be gone
        composeTestRule.onAllNodesWithText("Owner: Ava Nguyen →").assertCountEquals(0)

        // but John Smith is still there
        composeTestRule.onNodeWithText("Owner: John Smith →").assertIsDisplayed()
    }
}