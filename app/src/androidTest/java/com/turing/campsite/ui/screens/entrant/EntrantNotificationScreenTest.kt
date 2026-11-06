package com.turing.campsite.ui.screens.entrant

import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText

/**
 * EntrantNotificationScreen UI tests
 *
 * Tests if screen load and show correct headers. no buttons here to test
 */
class EntrantNotificationScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()



    /**
     * Tests if main header text is shown
     */
    @Test
    fun entrantNotificationScreen_rendersHeader() {

        composeTestRule.setContent {
            EntrantNotificationContent(notifications = emptyList())

        }

        // check main title
        composeTestRule.onNodeWithText("Notifications").assertIsDisplayed()

    }



    /**
     * Tests if section headers are shown
     */
    @Test
    fun entrantNotificationScreen_rendersSections() {


        composeTestRule.setContent {

            EntrantNotificationContent(notifications = emptyList())
        }


        // check the list header and the unread count exist
        composeTestRule.onNodeWithText("Recent").assertIsDisplayed()
        composeTestRule.onNodeWithText("0 unread").assertIsDisplayed()

    }

}