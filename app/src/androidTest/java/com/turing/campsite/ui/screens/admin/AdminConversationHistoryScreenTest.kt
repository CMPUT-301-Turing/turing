package com.turing.campsite.ui.screens.admin

import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription



/**
 * AdminConversationHistoryScreen UI tests
 *
 * Tests back button and null case
 */
class AdminConversationHistoryScreenTest {

    @get:Rule

    val composeTestRule = createComposeRule()

    /**
     * Tests the back button
     */
    @Test
    fun adminConversationHistoryScreen_backButtonClicks() {

        var clicked = false

        composeTestRule.setContent {

            // sample messages are used, so the normal screen shows
            AdminConversationHistoryScreen(
                onBackClick = { clicked = true }
            )
        }

        // find back button and click
        composeTestRule.onNodeWithContentDescription("Back to message logs").performClick()
        assertTrue(clicked)
    }


    /**
     * Tests that no messages shows the not found message
     */
    @Test
    fun adminConversationHistoryScreen_showsNotFoundForBadId() {

        composeTestRule.setContent {

            // pass no messages to trigger the not found message
            AdminConversationHistoryScreen(

                messages = emptyList(),
                onBackClick = {}
            )
        }

        // check error text
        composeTestRule.onNodeWithText("Conversation not found.").assertIsDisplayed()
    }
}