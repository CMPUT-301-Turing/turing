package com.turing.campsite.ui.screens.admin

import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue


/**
 * AdminNotificationLogScreen UI test
 *
 * These tests verify that the notification logs display correctly
 * the back button works and the filter options actually filter the list also correctly
 */
class AdminNotificationLogScreenTest {



    // for the cleanup after the test so no piling up
    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Tests that the headers search bar and sample notifiction data are visible
     */
    @Test
    fun adminNotificationLogScreen_displaysCorrectTextWorks() {
        composeTestRule.setContent {

            AdminNotificationLogScreen(onBackClick = {})
        }

        // HEADER AND SEARCH BAR
        composeTestRule.onNodeWithText("Notification Logs").assertIsDisplayed()

        composeTestRule.onNodeWithText("Search by name or email").assertIsDisplayed()

        composeTestRule.onNodeWithText("October 24, 2026").assertIsDisplayed()



        // NOTIFICATION

        composeTestRule.onNodeWithText("Sent to: John Doe").assertIsDisplayed()

        composeTestRule.onNodeWithText("Sent to: All campers").assertIsDisplayed()
    }

    /**
     * Tests that clicking the back arrow triggers the onBackClick callback
     */
    @Test
    fun adminNotificationLogScreen_backButtonClicksWorks() {

        var backWasClicked = false

        composeTestRule.setContent {

            AdminNotificationLogScreen(
                onBackClick = { backWasClicked = true }
            )
        }



        // finds the back button by name and clicks it
        composeTestRule.onNodeWithContentDescription("Back to admin home").performClick()


        // back button
        assertTrue(backWasClicked)
    }

    /**
     * Tests that clicking the lottery  filter button hides the
     * nothing non lottery
     */
    @Test
    fun adminNotificationLogScreen_filterButtonWorks() {


        composeTestRule.setContent {
            AdminNotificationLogScreen(onBackClick = {})
        }

        // all selected so announcement already visible?
        composeTestRule.onNodeWithText("Sent to: All campers").assertIsDisplayed()


        // perform click on lottery button
        composeTestRule.onNodeWithText("Lottery").performClick()



        // announcement gone?
        composeTestRule.onNodeWithText("Sent to: All campers").assertDoesNotExist()

        // lot notification still visible?
        composeTestRule.onNodeWithText("Sent to: John Doe").assertIsDisplayed()
    }
}