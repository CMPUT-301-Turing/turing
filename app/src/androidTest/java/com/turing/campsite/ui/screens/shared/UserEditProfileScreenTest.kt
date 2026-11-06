package com.turing.campsite.ui.screens.shared

import org.junit.Rule
import org.junit.Test

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription

import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick

import org.junit.Assert.assertTrue




/**
 * UserEditProfileScreen UI tests
 *
 * Tests the buttons to make sure they work
 */
class UserEditProfileScreenTest {



    @get:Rule
    val composeTestRule = createComposeRule()



    /**
     * Tests back button
     */
    @Test
    fun userEditProfileScreen_backButtonClicks() {

        var backClicked = false


        composeTestRule.setContent {
            UserEditProfileScreen(

                onBackClick = { backClicked = true }
            )

        }



        // find back to profile button and click it
        composeTestRule.onNodeWithContentDescription("Back to profile").performClick()



        // check if really pressed
        assertTrue(backClicked)

    }



    /**
     * Tests cancel button
     */
    @Test
    fun userEditProfileScreen_cancelButtonClicks() {

        var cancelClicked = false



        composeTestRule.setContent {
            UserEditProfileScreen(

                onBackClick = { cancelClicked = true }

            )



        }


        composeTestRule.onNodeWithText("Cancel").performClick()


        assertTrue(cancelClicked)
    }

}