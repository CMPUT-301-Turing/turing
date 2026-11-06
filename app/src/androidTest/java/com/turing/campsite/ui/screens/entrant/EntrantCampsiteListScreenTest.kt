package com.turing.campsite.ui.screens.entrant

import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText


import com.turing.campsite.ui.viewmodel.ReservationViewModel

/**
 * EntrantCampsiteListScreen UI tests
 */
class EntrantCampsiteListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun entrantCampsiteListScreen_rendersHeader() {


        // move view model outside the setContent block
        val reservationViewModel = ReservationViewModel()

        composeTestRule.setContent {
            EntrantCampsiteListScreen(
                reservationViewModel = reservationViewModel
            )
        }

        composeTestRule.onNodeWithText("Campsite lists").assertIsDisplayed()
    }
}