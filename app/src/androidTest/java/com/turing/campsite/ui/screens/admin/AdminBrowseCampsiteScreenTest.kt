package com.turing.campsite.ui.screens.admin

import org.junit.Rule
import org.junit.Test
import com.turing.campsite.ui.components.SEARCH_FIELD_TAG
import com.turing.campsite.data.model.Campsite
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.onNodeWithTag
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

    /**
     * Tests that typing in the search box hides the campsites that do not match
     */
    @Test
    fun adminBrowseCampsiteScreen_searchFiltersList() {
        val pinewood = Campsite(id = "c1", name = "Pinewood Retreat", location = "Jasper, Alberta")
        val heather = Campsite(id = "c2", name = "Heather Ridge", location = "Drumheller, Alberta")

        composeTestRule.setContent {
            AdminBrowseCampsiteScreen(
                onBackClick = {},
                campsites = listOf(pinewood, heather)
            )
        }

        composeTestRule.onNodeWithTag(SEARCH_FIELD_TAG).performTextInput("Heather")

        composeTestRule.onNodeWithText("Heather Ridge").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pinewood Retreat").assertDoesNotExist()
    }
}