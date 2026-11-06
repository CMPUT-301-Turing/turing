package com.turing.campsite.ui.screens.owner

import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue




/**
 * OwnerRegisterNewCampsiteScreen UI tests
 *
 * Tests back button and publish button to make sure callbacks work
 */
class OwnerRegisterNewCampsiteScreenTest {


    @get:Rule
    val composeTestRule = createComposeRule()


    /**
     * Tests back button click
     */

    @Test
    fun ownerRegisterNewCampsiteScreen_backButtonClicks() {


        var clicked = false

        composeTestRule.setContent {

            OwnerRegisterNewCampsiteScreen(

                onBackClick = { clicked = true }

            )

        }

        // find back button icon by description
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        assertTrue(clicked)
    }

    /**
     * Tests publish button click
     */
    @Test
    fun ownerRegisterNewCampsiteScreen_publishButtonClicks() {

        var clicked = false

        composeTestRule.setContent {

            OwnerRegisterNewCampsiteScreen(
                onBackClick = {},

                onPublishClick = { clicked = true }
            )

        }


        // find publish button by text
        composeTestRule.onNodeWithText("Publish").performClick()

        assertTrue(clicked)
    }

}