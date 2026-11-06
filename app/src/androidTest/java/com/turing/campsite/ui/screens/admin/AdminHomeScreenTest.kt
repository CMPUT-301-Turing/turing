package com.turing.campsite.ui.screens.admin

import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue

/**
 * AdminHomeScreen UI tests
 *
 * Tests 3 out of 6 important buttons to make sure they trigger the callback
 */
class AdminHomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()



    /**
     * Tests settings button
     */
    @Test
    fun adminHomeScreen_settingsButtonClicks() {

        var clickedB = false

        composeTestRule.setContent {

            AdminHomeScreen(
                onSettingsClick = { clickedB = true },

                onCampsitesClick = {},
                onProfilesClick = {},
                onImagesClick = {},


                onMessageLogsClick = {},
                onNotificationLogsClick = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Settings").performClick()


        assertTrue(clickedB)
    }

    /**
     * Tests browse campsites button
     */
    @Test
    fun adminHomeScreen_campsitesClicks() {
        var clickedB = false
        composeTestRule.setContent {

            AdminHomeScreen(
                onSettingsClick = {},


                onCampsitesClick = { clickedB = true },
                onProfilesClick = {},
                onImagesClick = {},


                onMessageLogsClick = {},
                onNotificationLogsClick = {}
            )
        }

        composeTestRule.onNodeWithText("Browse Campsites").performClick()
        assertTrue(clickedB)
    }



    /**
     * Tests profiles button
     */
    @Test
    fun adminHomeScreen_profilesButtonClicks() {

        var clickedB = false

        composeTestRule.setContent {

            AdminHomeScreen(
                onSettingsClick = {},

                onCampsitesClick = {},
                onProfilesClick = { clickedB = true },
                onImagesClick = {},


                onMessageLogsClick = {},
                onNotificationLogsClick = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Browse Profiles").performClick()


        assertTrue(clickedB)
    }












}
