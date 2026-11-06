package com.turing.campsite.ui.screens.owner

import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue




/**
 * OwnerEditCampsiteScreen UI tests
 *
 * Tests pretty much all buttons on the screen
 */
class OwnerEditCampsiteScreenTest {



    @get:Rule
    val composeTestRule = createComposeRule()



    /**
     * Tests back button
     */
    @Test
    fun ownerEditCampsiteScreen_backButtonClicks() {

        var backClicked = false


        composeTestRule.setContent {



            OwnerEditCampsiteScreen(

                onBackClick = { backClicked = true }

            )
        }


        composeTestRule.onNodeWithContentDescription("Back").performClick()


        assertTrue(backClicked)
    }



    /**
     * Tests update button
     */
    @Test
    fun ownerEditCampsiteScreen_updateButtonClicks() {

        var updateClicked = false


        composeTestRule.setContent {

            OwnerEditCampsiteScreen(
                onBackClick = {},
                onUpdateClick = { updateClicked = true }

            )

        }


        composeTestRule.onNodeWithText("Update").performClick()


        assertTrue(updateClicked)
    }



    /**
     * Tests cancel button
     */
    @Test
    fun ownerEditCampsiteScreen_cancelButtonClicks() {

        var cancelClicked = false



        composeTestRule.setContent {

            OwnerEditCampsiteScreen(
                onBackClick = { cancelClicked = true }
            )
        }



        // find cancel and click it
        composeTestRule.onNodeWithText("Cancel").performClick()



        assertTrue(cancelClicked)

    }

}