package com.turing.campsite.ui.screens.shared

import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import com.turing.campsite.data.model.User
import org.junit.Assert.assertEquals
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
     * Shows the entrant profile for a user with the given notification setting
     */
    private fun setProfileWithNotifications(
        user: User?,
        onNotificationsChange: (Boolean) -> Unit = {}
    ) {
        composeTestRule.setContent {
            UserProfileScreen(
                currentView = UserView.ENTRANT,
                user = user,
                onNotificationsChange = onNotificationsChange,
                onEditProfileClick = {},
                onSwitchViewClick = {},
                onBackClick = {}
            )
        }
    }



    /**
     * Tests that notifications are on for a new user
     */
    @Test
    fun userProfileScreen_notificationsOnByDefault() {
        setProfileWithNotifications(User(id = "u1"))

        composeTestRule.onNodeWithText("Receive notifications").assertIsOn()
    }



    /**
     * Tests that an opted out user sees the switch off
     */
    @Test
    fun userProfileScreen_optedOutShowsOff() {
        setProfileWithNotifications(User(id = "u1", notificationsEnabled = false))

        composeTestRule.onNodeWithText("Receive notifications").assertIsOff()
        composeTestRule.onNodeWithText("You opted out. You won't get any notifications.").assertExists()
    }



    /**
     * Tests that tapping the switch opts the user out
     */
    @Test
    fun userProfileScreen_tapSwitchOptsOut() {
        var newSetting: Boolean? = null
        setProfileWithNotifications(User(id = "u1")) { newSetting = it }

        composeTestRule.onNodeWithText("Receive notifications").performScrollTo().performClick()

        assertEquals(false, newSetting)
    }



    /**
     * Tests that tapping the switch again turns notifications back on
     */
    @Test
    fun userProfileScreen_tapSwitchOptsBackIn() {
        var newSetting: Boolean? = null
        setProfileWithNotifications(User(id = "u1", notificationsEnabled = false)) { newSetting = it }

        composeTestRule.onNodeWithText("Receive notifications").performScrollTo().performClick()

        assertEquals(true, newSetting)
    }



    /**
     * Tests that the switch can't be used before the profile loads
     */
    @Test
    fun userProfileScreen_switchDisabledWhileLoading() {
        setProfileWithNotifications(null)

        composeTestRule.onNodeWithText("Receive notifications").assertIsNotEnabled()
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



    /**
     * Tests the switch to admin view button
     */
    @Test
    fun userProfileScreen_switchToAdminClicks() {
        var adminClicked = false

        composeTestRule.setContent {
            UserProfileScreen(
                currentView = UserView.ENTRANT,
                onEditProfileClick = {},
                onSwitchViewClick = {},
                onBackClick = {},
                onSwitchToAdminClick = { adminClicked = true }
            )
        }

        composeTestRule.onNodeWithText("Switch to admin view")
            .performScrollTo()
            .performClick()

        assertTrue(adminClicked)
    }

    /**
     * Tests that the admin button is hidden when there is no admin callback
     */
    @Test
    fun userProfileScreen_noAdminButtonWithoutCallback() {
        composeTestRule.setContent {
            UserProfileScreen(
                currentView = UserView.OWNER,
                onEditProfileClick = {},
                onSwitchViewClick = {},
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithText("Switch to admin view").assertDoesNotExist()
    }
}
