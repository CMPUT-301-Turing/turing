package com.turing.campsite.ui.screens.owner

import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue

/**
 * OwnerHomeScreen UI tests
 *
 * Tests pretty much all buttons on the screen
 */
class OwnerHomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()



    /**
     * Tests register campsite button
     */
    @Test
    fun ownerHomeScreen_registerCampsiteClicks() {

        var clicked = false


        composeTestRule.setContent {

            OwnerHomeScreen(
                onNotificationsClick = {},
                onProfileClick = {},

                onRegisterCampsiteClick = { clicked = true },

                onCreateLotteryClick = {},
                onCampsiteClick = {}
            )

        }




        composeTestRule.onNodeWithText("+ Register New Campsite").performClick()


        assertTrue(clicked)
    }



    /**
     * Tests create lottery button
     */
    @Test
    fun ownerHomeScreen_createLotteryClicks() {

        var clicked = false

        composeTestRule.setContent {

            OwnerHomeScreen(

                onNotificationsClick = {},
                onProfileClick = {},
                onRegisterCampsiteClick = {},

                onCreateLotteryClick = { clicked = true },

                onCampsiteClick = {}
            )

        }


        composeTestRule.onNodeWithText("Create Lottery").performClick()


        assertTrue(clicked)
    }



    /**
     * Tests notifications icon button
     */
    @Test
    fun ownerHomeScreen_notificationsClicks() {

        var clicked = false


        composeTestRule.setContent {

            OwnerHomeScreen(

                onNotificationsClick = { clicked = true },

                onProfileClick = {},
                onRegisterCampsiteClick = {},
                onCreateLotteryClick = {},
                onCampsiteClick = {}
            )

        }


        // find by icon description
        composeTestRule.onNodeWithContentDescription("Notifications").performClick()


        assertTrue(clicked)
    }
}