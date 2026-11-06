package com.turing.campsite.ui.screens.owner

import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Assert.assertTrue

/**
 * OwnerEditLotteryScreen UI tests
 *
 * Tests the buttons to make sure they work
 */
class OwnerEditLotteryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()



    /**
     * Tests back button
     */
    @Test
    fun ownerEditLotteryScreen_backButtonClicks() {

        var backClicked = false


        composeTestRule.setContent {

            OwnerEditLotteryScreen(
                onBackClick = { backClicked = true }
            )
        }


        // find back button and click it
        composeTestRule.onNodeWithContentDescription("Back").performClick()


        assertTrue(backClicked)
    }



    /**
     * Tests update button
     */
    @Test
    fun ownerEditLotteryScreen_updateButtonClicks() {

        var updateClicked = false


        composeTestRule.setContent {

            OwnerEditLotteryScreen(
                onBackClick = {},

                onUpdateClick = { updateClicked = true }

            )
        //
        }


        composeTestRule.onNodeWithText("Update Lottery").performClick()


        assertTrue(updateClicked)
    }



    /**
     * Tests title on screen
     */
    @Test
    fun ownerEditLotteryScreen_rendersTitle() {

        composeTestRule.setContent {

            OwnerEditLotteryScreen(

                onBackClick = {}
            )
        }



        //c
        composeTestRule.onNodeWithText("Edit Lottery").assertIsDisplayed()


    }
}