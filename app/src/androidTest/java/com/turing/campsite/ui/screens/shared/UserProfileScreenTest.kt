package com.turing.campsite.ui.screens.shared

import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription

import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
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
     * Tests back button in owner view
     */

    @Test
    fun userProfileScreen_backButtonClicks() {

        var backClicked = false


        composeTestRule.setContent {


            UserProfileScreen(
                    currentView = UserView.OWNER,

                    onEditProfileClick = {},

                    onPolicyClick = {},

                    onSwitchViewClick = {},
                    onBackClick = { backClicked = true }

            )


        }


        // search for back to owner home button
        composeTestRule.onNodeWithContentDescription("Back to owner home").performClick()


        // check if back button clicked
        assertTrue(backClicked) }



    /**
     * Tests that the app policy option opens the policy screen
     */
    @Test
    fun userProfileScreen_policyClicks() {
        var policyClicked = false

        composeTestRule.setContent {
            UserProfileScreen(
                currentView = UserView.ENTRANT,
                onEditProfileClick = {},
                onPolicyClick = { policyClicked = true },
                onSwitchViewClick = {},
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithText("View app policy and guidelines")
            .performScrollTo()
            .performClick()

        assertTrue(policyClicked)
    }



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

                    onPolicyClick = {},


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