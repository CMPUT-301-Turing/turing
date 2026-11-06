package com.turing.campsite.ui.screens.admin

import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue

/**
 * AdminBrowseCampsiteScreen UI tests
 *
 * Tests back button and if title load. no hardcoded data check
 */
class AdminBrowseCampsiteScreenTest {



    @get:Rule
    val composeTestRule = createComposeRule()

    
    /**
     * Tests back button
     */
    @Test
    fun adminBrowseCampsiteScreen_backButtonClicks() {

        var clickedB = false

        composeTestRule.setContent {

            AdminBrowseCampsiteScreen(
                onBackClick = { clickedB = true }
            )
        }

        // find back button and click
        composeTestRule.onNodeWithContentDescription("Back to admin home").performClick()
        assertTrue(clickedB)
    }

    /**
     * Tests that title is shown on screen
     */
    @Test
    fun adminBrowseCampsiteScreen_rendersTitle() {
        composeTestRule.setContent {
            AdminBrowseCampsiteScreen(
                onBackClick = {}
            )
        }

        // check title is there
        composeTestRule.onNodeWithText("Browse Campsites").assertIsDisplayed()
    }
}