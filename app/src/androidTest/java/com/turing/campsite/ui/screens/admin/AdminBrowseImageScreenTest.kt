package com.turing.campsite.ui.screens.admin

import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue

/**
 * AdminBrowseImageScreen UI tests
 *
 * Tests back button and filter buttons to make sure they click without crash
 */
class AdminBrowseImageScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Tests back button click
     */
    @Test
    fun adminBrowseImageScreen_backButtonClicks() {
        var clicked = false
        composeTestRule.setContent {
            AdminBrowseImageScreen(
                onBackClick = { clicked = true }
            )
        }

        // find back button and click it
        composeTestRule.onNodeWithContentDescription("Back to admin home").performClick()
        assertTrue(clicked)
    }

    /**
     * Tests filter button click
     */
    @Test
    fun adminBrowseImageScreen_filterButtonClicks() {
        composeTestRule.setContent {
            AdminBrowseImageScreen(
                onBackClick = {}
            )
        }

        // find campsites filter and click it
        composeTestRule.onNodeWithText("Campsites").performClick()

        // find profiles filter and click it
        composeTestRule.onNodeWithText("Profiles").performClick()
    }
}