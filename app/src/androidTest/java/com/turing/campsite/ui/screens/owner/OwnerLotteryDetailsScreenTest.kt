package com.turing.campsite.ui.screens.owner

import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.performScrollToNode
import org.junit.Assert.assertTrue

/**
 * OwnerLotteryDetailsScreen UI tests
 *
 * Tests back button and send notification button to make sure callbacks work
 */
class OwnerLotteryDetailsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()



    /**
     * Tests back button
     */
    @Test
    fun ownerLotteryDetailsScreen_backButtonClicks() {

        var clickedN = false


        composeTestRule.setContent {

            OwnerLotteryDetailsScreen(
                onBackClick = { clickedN = true },

                onSendNotificationClick = {}
            )
        }


        composeTestRule.onNodeWithContentDescription("Back to campsite details").performClick()


        assertTrue(clickedN)
    }



    /**
     * Tests send notification button
     */
    @Test
    fun ownerLotteryDetailsScreen_sendNotificationButtonClicks() {

        var clickedA = false


        composeTestRule.setContent {

            OwnerLotteryDetailsScreen(
                onBackClick = {},

                onSendNotificationClick = { clickedA = true }
            )

        }


        // click the send button
        // the button is at the bottom of a lazy list, so scroll the list to it first
        composeTestRule.onAllNodes(hasScrollAction()).onFirst()
            .performScrollToNode(hasText("Send Notification to Waitlist"))

        composeTestRule.onNodeWithText("Send Notification to Waitlist").performClick()


        assertTrue(clickedA)
    }
}