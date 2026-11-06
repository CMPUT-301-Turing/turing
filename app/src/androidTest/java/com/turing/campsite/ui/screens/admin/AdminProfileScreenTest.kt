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
 * AdminProfileScreen UI test
 *
 * These tests verify that the correct text is displayed and that clicks
 * to the  certain buttons triggers the correct navigation callbacks
 */
class AdminProfileScreenTest {


    // so I can drop each screen wafter test
    @get:Rule
    val composeTestRule = createComposeRule()


    /**
     * Verifies that the profile details and headers from settings are visible on the screen
     */
    @Test
    fun adminProfileScreen_displaysCorrectText() {

        // empty screen with callbacks to check text
        composeTestRule.setContent{
            AdminProfileScreen(
                onBackClick = {},

                onSwitchToOwnerClick = {},

                onSwitchToEntrantClick = {}
            )

        }



        // TODO: come back to this once the firebase is set up
        // because I used data that is hardcoded so if the test fails its because of that

        // check if text elements that are important actually exist
        composeTestRule.onNodeWithText("Admin Profile/Settings").assertIsDisplayed()
        composeTestRule.onNodeWithText("Morgan Ellis").assertIsDisplayed()
        composeTestRule.onNodeWithText("morgan.ellis@campmail.com").assertIsDisplayed()
        composeTestRule.onNodeWithText("Administrator").assertIsDisplayed()
        composeTestRule.onNodeWithText("Switch View").assertIsDisplayed()
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
        composeTestRule.onNodeWithText("Log Out").assertIsDisplayed()
    }

    /**
     * Verifies that back arrow click triggers the onBackClick callback
     */
    @Test
    fun adminProfileScreen_backButtonClicks() {
        var backWasClicked = false

        composeTestRule.setContent {
            AdminProfileScreen(
                onBackClick = { backWasClicked = true },

                onSwitchToOwnerClick = {},
                onSwitchToEntrantClick = {}
            )
        }

        // finds the back button
        composeTestRule.onNodeWithContentDescription("Back to admin home").performClick()

        // checks that the callback was triggered
        assertTrue(backWasClicked)
    }

    /**
     * Verifies clicking the switch to campsite owner button actually triggers callback.
     */
    @Test
    fun adminProfileScreen_switchToOwnerClicks() {
        var switchToOwnerWasClicked = false

        composeTestRule.setContent {

            AdminProfileScreen(

                onBackClick = {},

                onSwitchToOwnerClick = { switchToOwnerWasClicked = true },
                onSwitchToEntrantClick = {}
            )
        }

        // finds the button by its text and clicks it
        composeTestRule.onNodeWithText("Switch to Campsite Owner").performClick()

        assertTrue(switchToOwnerWasClicked)
    }



    /**
     * Verfies that clicking button switch to entrant triggers its callback
     */
    @Test
    fun adminProfileScreen_switchToEntrantClicks() {

        var switchToEntrantWasClicked = false

        composeTestRule.setContent {

            AdminProfileScreen(

                onBackClick = {},

                onSwitchToOwnerClick = {},
                onSwitchToEntrantClick = { switchToEntrantWasClicked = true }
            )
        }

        composeTestRule.onNodeWithText("Switch to Entrant").performClick()

        //
        assertTrue(switchToEntrantWasClicked)
    }
}