/* Checks navigation callbacks exposed by the campsite details UI. */
package com.turing.campsite.ui.screens.owner

import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText

import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import org.junit.Assert.assertTrue
import com.turing.campsite.data.model.Campsite



/**
 * OwnerCampsiteFullDetailScreen UI tests
 *
 * Tests pretty much all buttons on the screen
 */

class OwnerCampsiteFullDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testCampsite = Campsite(
        id = "c1",
        name = "Riverbend Glade",
        location = "Dartmoor, Devon",
        maxOccupancy = 10,
        description = "Beautiful riverside cabin with a lot room for your friends.",
        tags = listOf("Riverside", "Party Friendly", "Pets Allowed"),
        rulesText = "Don't leave the doors unlocked"
    )



    /**
     * Tests back button
     */

    @Test
    fun ownerCampsiteFullDetailScreen_backButtonClicks()
    {

        var backClicked = false


        composeTestRule.setContent {

            OwnerCampsiteFullDetailScreen(
                campsite = testCampsite,


                onBackClick = { backClicked = true },


                onEditClick = {},
                onQRCodeClick = {},
                
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
                campsite = testCampsite,
                onBackClick = {},

                onEditClick = { editClicked = true },

                onQRCodeClick = {},

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
                campsite = testCampsite,

                onBackClick = {},
                onEditClick = {},
                onQRCodeClick = {},

                onLotteryClick = { lotteryClicked = true },

                onReservationsClick = {}
            )

        }


        // the link is at the bottom of the page, so scroll down to it first
        composeTestRule.onNodeWithText("View Waitlist").performScrollTo()

        // click the button
        composeTestRule.onNodeWithText("View Waitlist").performClick()


        // check if clicked
        assertTrue(lotteryClicked)
    }
}