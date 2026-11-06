package com.turing.campsite.ui.screens.admin

import org.junit.Rule
import org.junit.Test
import com.turing.campsite.ui.components.SEARCH_FIELD_TAG
import com.turing.campsite.data.model.User
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.onNodeWithTag
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

    /**
     * Tests that typing in the search box hides the profiles that do not match
     */
    @Test
    fun adminBrowseProfilesScreen_searchFiltersList() {
        val amelia = User(id = "u1", name = "Amelia Clarke", email = "amelia@example.com")
        val noah = User(id = "u2", name = "Noah Patel", email = "noah@example.com")

        composeTestRule.setContent {
            AdminBrowseProfilesScreen(
                onBackClick = {},
                profiles = listOf(amelia, noah)
            )
        }

        // search by email
        composeTestRule.onNodeWithTag(SEARCH_FIELD_TAG).performTextInput("noah@")

        composeTestRule.onNodeWithText("Noah Patel").assertIsDisplayed()
        composeTestRule.onNodeWithText("Amelia Clarke").assertDoesNotExist()
    }
}