package com.turing.campsite.ui.screens.owner

import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText

import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue





/**
 * OwnerCampsiteHistoryScreen UI tests
 *
 * Tests pretty much all buttons on the screen
 */

class OwnerCampsiteHistoryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()



    /**
     * Tests back button
     */
    @Test
    fun ownerCampsiteHistoryScreen_backButtonClicks() {



        var backClicked = false



        composeTestRule.setContent {

            OwnerCampsiteHistoryScreen(

                onBackClick = { backClicked = true },

                onMessageClick = {},
                onRejectClick = {}
            )
        }



        composeTestRule.onNodeWithContentDescription("Back to campsite details").performClick()


        assertTrue(backClicked)
    }



    /**
     * Tests switching tab to past
     */
    @Test
    fun ownerCampsiteHistoryScreen_pastTabClicks() {

        composeTestRule.setContent {

            OwnerCampsiteHistoryScreen(
                onBackClick = {},
                onMessageClick = {},

                onRejectClick = {}
            )

        }


        composeTestRule.onNodeWithText("Past").performClick()


    }



    /**
     * Tests reject button
     */
    @Test
    fun ownerCampsiteHistoryScreen_rejectButtonClicks() {

        var rejectClicked = false


        composeTestRule.setContent {

            OwnerCampsiteHistoryScreen(
                onBackClick = {},
                onMessageClick = {},

                onRejectClick = { rejectClicked = true }
            )



        }


        // there is more than one reject button so we click the first one
        composeTestRule.onAllNodesWithText("Reject")[0].performClick()


        assertTrue(rejectClicked)
    }
}