package com.turing.campsite.ui.screens.entrant

import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText

/**
 * EntrantQRCodeScreen UI tests
 *
 * Tests if screen loads. no buttons here to test
 */
class EntrantQRCodeScreenTest {

    @get:Rule

    val composeTestRule = createComposeRule()



    /**
     * Tests if header text shows
     */
    @Test
    fun entrantQRCodeScreen_rendersHeader() {
        composeTestRule.setContent {

            EntrantQRCodeScreen()
        }

        // check main title
        composeTestRule.onNodeWithText("Scan QR code").assertIsDisplayed()
    }



    /**
     * Tests if instruction text shows
     */
    @Test
    fun entrantQRCodeScreen_rendersInstruction() {

        composeTestRule.setContent {

            EntrantQRCodeScreen()
        }



        // check instruction text at bottom
        composeTestRule.onNodeWithText("Align a QR code within the frame.").assertIsDisplayed()
    }
}