package com.turing.campsite.ui.screens.owner

import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick

import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Assert.assertTrue



/**
 * OwnerNotificationScreen UI tests
 *
 * Tests the buttons to make sure they work
 */
class OwnerNotificationScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()



    /**
     * Tests back button
     */
    @Test
    fun ownerNotificationScreen_backButtonClicks() {

        var clicked = false


        composeTestRule.setContent {

            OwnerNotificationScreen(

                onBackClick = { clicked = true }
            )
        }


        composeTestRule.onNodeWithContentDescription("Back to owner home").performClick()


        assertTrue(clicked)
    }



    /**
     * Tests plus button and send button
     */
    @Test
    fun ownerNotificationScreen_plusAndSendClicks() {

        composeTestRule.setContent {

            OwnerNotificationScreen(
                onBackClick = {}
            )

        }


        // click plus button
        composeTestRule.onNodeWithContentDescription("Create notification").performClick()





        // check sheet opened
        composeTestRule.onNodeWithText("New Notification").assertIsDisplayed()



        // click send button
        composeTestRule.onNodeWithText("Send").performClick()


    }
}