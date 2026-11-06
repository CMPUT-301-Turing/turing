package com.turing.campsite.ui.screens.owner

import org.junit.Rule
import org.junit.Test

import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue


import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription







/**
 * OwnerCampsiteQRCodeScreen UI tests
 *
 * Tests pretty much all buttons on the screen
 */
class OwnerCampsiteQRCodeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()



    /**
     * Tests back button
     */

    @Test
    fun ownerCampsiteQRCodeScreen_backButtonClicks()
    {

        var backClicked = false


        composeTestRule.setContent {

            OwnerCampsiteQRCodeScreen(



                onBackClick = { backClicked = true }
            )
        }


        composeTestRule.onNodeWithContentDescription("Back to campsite details").performClick()


        assertTrue(backClicked)
    }




    /**
     * Tests share button
     */

    @Test
    fun ownerCampsiteQRCodeScreen_shareButtonClicks() {

        var shareClicked = false


        composeTestRule.setContent {

            OwnerCampsiteQRCodeScreen(
                onBackClick = {},
                onShareClick = { shareClicked = true }
            )

        }



        // share button clicked
        composeTestRule.onNodeWithContentDescription("Share").performClick()



        // check if really clicked
        assertTrue(shareClicked)
    }






    /**
     * Tests download button
     */
    @Test
    fun ownerCampsiteQRCodeScreen_downloadButtonClicks() {

        var downloadClicked = false


        composeTestRule.setContent {


            OwnerCampsiteQRCodeScreen(
                onBackClick = {},

                onDownloadClick = { downloadClicked = true }
            )

        }



        // find download and click it
        composeTestRule.onNodeWithContentDescription("Download").performClick()



        // check if download was really clicked
        assertTrue(downloadClicked)
    }
}