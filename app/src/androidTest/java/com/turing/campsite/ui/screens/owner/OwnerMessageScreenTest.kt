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
 * OwnerMessageScreen UI tests
 *
 * Tests back button and send button and input field
 */
class OwnerMessageScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()



    /**
     * Tests back button
     */
    @Test
    fun ownerMessageScreen_backButtonClicks() {

        var clicked = false


        composeTestRule.setContent {

            OwnerMessageScreen(



                onBackClick = { clicked = true }
            )
        }


        composeTestRule.onNodeWithContentDescription("Back to reservations").performClick()


        assertTrue(clicked)
    }




    /**
     * Tests send button and input field
     */
    @Test
    fun ownerMessageScreen_sendButtonClicks() {

        composeTestRule.setContent {

            OwnerMessageScreen(




                onBackClick = {}
            )
        }


        // check input field is there
        composeTestRule.onNodeWithText("Type a message...").assertIsDisplayed()



        // click send button to make sure no crash
        composeTestRule.onNodeWithContentDescription("Send message").performClick()


    }
}