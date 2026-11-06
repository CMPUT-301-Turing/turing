package com.turing.campsite.ui.screens.entrant

import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.turing.campsite.ui.viewmodel.CampsiteViewModel

/**
 * EntrantHomeScreen UI tests
 *
 * Tests if the screen loads and shows the header
 */
class EntrantHomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Tests if screen loads and shows header text
     */
    @Test
    fun entrantHomeScreen_rendersHeader() {
        // create the view model outside the composable
        val campsiteViewModel = CampsiteViewModel()

        composeTestRule.setContent {
            EntrantHomeScreen(
                campsiteViewModel = campsiteViewModel,
                onCampsiteClick = {}
            )
        }

        // check main title
        composeTestRule.onNodeWithText("Campsites").assertIsDisplayed()
    }
}