package com.turing.campsite.ui.screens.entrant

import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.junit4.createComposeRule

import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue



/**
 * EntrantCampsiteDetailsScreen UI tests
 *
 * Tests back button and lottery button to make sure they click
 */
class EntrantCampsiteDetailsScreenTest {



    @get:Rule
    val composeTestRule = createComposeRule()




    /**
     * Tests back button click
     */

    @Test
    fun entrantCampsiteDetailsScreen_backButtonClicks() {
        var clicked = false

        composeTestRule.setContent {

            // use valid name so screen loads
            EntrantCampsiteDetailsScreen(

                campsiteName = "Pinewood Retreat",
                onBackClick = { clicked = true }
            )
        }

        composeTestRule.onNodeWithContentDescription("Back to campsites").performClick()
        assertTrue(clicked)
    }



    /**
     * Tests enter lottery button click
     */
    @Test
    fun entrantCampsiteDetailsScreen_enterLotteryClicks() {
        var clicked = false

        composeTestRule.setContent {

            EntrantCampsiteDetailsScreen(

                campsiteName = "Pinewood Retreat",

                onBackClick = {},
                onEnterLotteryClick = { clicked = true }

            )

        }

        composeTestRule.onNodeWithText("Enter lottery").performClick()

        assertTrue(clicked)
    }
}