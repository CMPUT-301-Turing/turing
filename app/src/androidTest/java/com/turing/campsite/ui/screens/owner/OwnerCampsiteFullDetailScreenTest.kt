package com.turing.campsite.ui.screens.owner

import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText

import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue



/**
 * OwnerCampsiteFullDetailScreen UI tests
 *
 * Tests pretty much all buttons on the screen
 */

class OwnerCampsiteFullDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()



    /**
     * Tests back button
     */

    @Test
    fun ownerCampsiteFullDetailScreen_backButtonClicks()
    {

        var backClicked = false


        composeTestRule.setContent {

            OwnerCampsiteFullDetailScreen(


                onBackClick = { backClicked = true },


                onEditClick = {},
                onQRCodeClick = {},
                onEditLotteryClick = {},
                
                onLotteryClick = {},
                onReservationsClick = {}
            )

        }


        // click button
        composeTestRule.onNodeWithContentDescription("Back to owner home").performClick()



        // check if clicked
        assertTrue(backClicked)
    }





    /**
     * Tests edit campsite button
     */

    @Test
    fun ownerCampsiteFullDetailScreen_editCampsiteClicks()
    {

        var editClicked = false



        composeTestRule.setContent {

            OwnerCampsiteFullDetailScreen(
                onBackClick = {},

                onEditClick = { editClicked = true },

                onQRCodeClick = {},
                onEditLotteryClick = {},

                onLotteryClick = {},
                onReservationsClick = {}
            )

        }


        // click
        composeTestRule.onNodeWithText("Edit Campsite").performClick()



        //check if clicked
        assertTrue(editClicked)
    }



    /**
     * Tests lottery link button
     */

    @Test
    fun ownerCampsiteFullDetailScreen_lotteryLinkClicks()
    {

        var lotteryClicked = false



        composeTestRule.setContent {


            OwnerCampsiteFullDetailScreen(

                onBackClick = {},
                onEditClick = {},
                onQRCodeClick = {},
                onEditLotteryClick = {},

                onLotteryClick = { lotteryClicked = true },

                onReservationsClick = {}
            )

        }


        // click the button
        composeTestRule.onNodeWithText("Lottery & Waitlist Details").performClick()


        // check if clicked
        assertTrue(lotteryClicked)
    }
}