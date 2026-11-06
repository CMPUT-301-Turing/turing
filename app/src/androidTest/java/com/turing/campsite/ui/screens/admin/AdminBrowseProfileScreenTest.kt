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
 * AdminBrowseProfilesScreen UI tests
 *
 * Tests back button and if placeholder load on screen
 */
class AdminBrowseProfilesScreenTest {


    @get:Rule
    val composeTestRule = createComposeRule()


    /**
     * Tests back button
     */
    @Test
    fun adminBrowseProfilesScreen_backButtonClicks() {

        var clickedB = false

        composeTestRule.setContent {
            AdminBrowseProfilesScreen(
                onBackClick = { clickedB = true }
            )
        }



        // find back button and click
        composeTestRule.onNodeWithContentDescription("Back to admin home").performClick()
        assertTrue(clickedB)
    }



    /**
     * Tests that search placeholder is shown
     */
    @Test
    fun adminBrowseProfilesScreen_showsSearchBar() {

        composeTestRule.setContent {
            AdminBrowseProfilesScreen(

                onBackClick = {}
            )
        }

        // check search text is there
        composeTestRule.onNodeWithText("Search by name or email").assertIsDisplayed()
    }
}