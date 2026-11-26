package com.turing.campsite.ui.screens.shared

import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import com.turing.campsite.data.model.User
import org.junit.Assert.assertEquals
import androidx.compose.ui.test.onNodeWithContentDescription

import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue



/**
 *
 * UserProfileScreen UI tests
 *
 *
 * Tests the buttons to make sure they work
 */

class UserProfileScreenTest {



    @get:Rule

    val composeTestRule = createComposeRule()



    /**
     * Tests that the signed-in user's details are shown
     */
    @Test
    fun userProfileScreen_showsUserDetails() {
        composeTestRule.setContent {
            UserProfileScreen(
                currentView = UserView.ENTRANT,
                user = User(id = "u1", name = "Alice", email = "alice@example.com", phone = "780-555-0100"),
                onEditProfileClick = {},
                onSwitchViewClick = {},
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithText("Alice").assertExists()
        composeTestRule.onNodeWithText("alice@example.com").assertExists()
        composeTestRule.onNodeWithText("780-555-0100").assertExists()
    }



    /**
     * Tests that empty details say "Not added"
     */
    @Test
    fun userProfileScreen_emptyDetailsSayNotAdded() {
        composeTestRule.setContent {
            UserProfileScreen(
                currentView = UserView.ENTRANT,
                user = User(id = "u1"),
                onEditProfileClick = {},
                onSwitchViewClick = {},
                onBackClick = {}
            )
        }

        // name, email and phone are all empty
        assertEquals(3, composeTestRule.onAllNodesWithText("Not added").fetchSemanticsNodes().size)
    }



    /**
     * Tests back button in owner view
     */

    @Test
    fun userProfileScreen_backButtonClicks() {

        var backClicked = false


        composeTestRule.setContent {


            UserProfileScreen(
                    currentView = UserView.OWNER,

                    onEditProfileClick = {},

                    onSwitchViewClick = {},
                    onBackClick = { backClicked = true }

            )


        }


        // search for back to owner home button
        composeTestRule.onNodeWithContentDescription("Back to owner home").performClick()


        // check if back button clicked
        assertTrue(backClicked) }



    /**
     * Tests switch view button in entrant view
     */

    @Test
    fun userProfileScreen_switchViewClicks()

    {


        var switchClicked = false


        composeTestRule.setContent {


            UserProfileScreen(
                    currentView = UserView.ENTRANT,

                    onEditProfileClick = {},


                    onSwitchViewClick = { switchClicked = true },


                    onBackClick = {}
            )

        }



        // search for a node with that text
        composeTestRule.onNodeWithText("Switch to campsite owner view").performClick()



        // check if really pressed
        assertTrue(switchClicked)
    }
}