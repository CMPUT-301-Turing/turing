package com.turing.campsite.ui.screens.owner

import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.onNodeWithText
import com.turing.campsite.ui.viewmodel.LotteryViewModel
import org.junit.Assert.assertTrue




/**
 * OwnerCreateLotteryScreen UI tests
 *
 * Tests the buttons to make sure they work
 */
class OwnerCreateLotteryScreenTest {


    @get:Rule


    val composeTestRule = createComposeRule()



    /**
     * Tests back button
     */
    @Test
    fun ownerCreateLotteryScreen_backButtonClicks()
    {

        var backClicked = false
        val lotteryViewModel = LotteryViewModel()


        composeTestRule.setContent {


            OwnerCreateLotteryScreen(
                onBackClick = { backClicked = true },
                lotteryViewModel = lotteryViewModel

            )
        }


        composeTestRule.onNodeWithContentDescription("Back").performClick()


        assertTrue(backClicked)
    }



    /**
     * Tests publish button
     */
    @Test
    fun ownerCreateLotteryScreen_publishButtonClicks() {


        //
        var publishClicked = false
        val lotteryViewModel = LotteryViewModel()


        composeTestRule.setContent {

            OwnerCreateLotteryScreen(

                onBackClick = {},
                onPublishClick = { publishClicked = true },
                lotteryViewModel = lotteryViewModel

            )

        }


        composeTestRule.onNodeWithText("Publish Lottery").performClick()


        assertTrue(publishClicked)
    }
}