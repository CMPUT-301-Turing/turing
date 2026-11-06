package com.turing.campsite.ui.screens.entrant

import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.turing.campsite.ui.viewmodel.CampsiteViewModel
import com.turing.campsite.ui.viewmodel.LotteryViewModel

/**
 * EntrantCampsiteDetailsScreen UI tests
 */
class EntrantCampsiteDetailsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun entrantCampsiteDetailsScreen_rendersHeader() {
        // move view models outside the setContent block
        val campsiteViewModel = CampsiteViewModel()
        val lotteryViewModel = LotteryViewModel()

        composeTestRule.setContent {
            EntrantCampsiteDetailsScreen(
                campsiteViewModel = campsiteViewModel,
                lotteryViewModel = lotteryViewModel,
                campsiteId = "test-id",
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithText("Campsites").assertIsDisplayed()
    }
}