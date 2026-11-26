/* Checks callbacks on the reusable lottery details screen; it does not send notifications. */
package com.turing.campsite.ui.screens.owner

import com.turing.campsite.data.model.Lottery
import com.turing.campsite.data.model.LotteryEntry
import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.hasText
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
     * Tests send notification navigation with an explicit waitlist fixture.
     */
    @Test
    fun ownerLotteryDetailsScreen_sendNotificationButtonClicks() {

        var clickedA = false


        composeTestRule.setContent {

            OwnerLotteryDetailsScreen(
                onBackClick = {},

                onSendNotificationClick = { clickedA = true },
                lottery = Lottery(id = "lottery-1"),
                entries = listOf(LotteryEntry(id = "entrant-1", entrantId = "entrant-1"))
            )

        }


        // the button is a LazyColumn item, which is not composed until it is scrolled into view
        composeTestRule.onNode(hasScrollToNodeAction())
            .performScrollToNode(hasText("Send Notification to Waitlist"))

        // click the send button
        composeTestRule.onNodeWithText("Send Notification to Waitlist").performClick()


        assertTrue(clickedA)
    }
}