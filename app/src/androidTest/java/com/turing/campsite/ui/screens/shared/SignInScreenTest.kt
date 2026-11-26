package com.turing.campsite.ui.screens.shared

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * SignInScreen UI test.
 *
 * Checks the loading and error states, and that "Try again" calls its callback.
 */
class SignInScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun signInScreen_whileSigningIn_showsLoadingText() {
        composeTestRule.setContent {
            SignInScreen(errorMessage = null, onRetryClick = {})
        }

        composeTestRule.onNodeWithText("Signing in...").assertIsDisplayed()
    }

    @Test
    fun signInScreen_afterFailure_showsError() {
        composeTestRule.setContent {
            SignInScreen(errorMessage = "No internet connection", onRetryClick = {})
        }

        composeTestRule.onNodeWithText("Could not sign in").assertIsDisplayed()
        composeTestRule.onNodeWithText("No internet connection").assertIsDisplayed()
    }

    @Test
    fun signInScreen_tryAgainClick_callsRetry() {
        var retried = false

        composeTestRule.setContent {
            SignInScreen(errorMessage = "No internet connection", onRetryClick = { retried = true })
        }

        composeTestRule.onNodeWithText("Try again").performClick()

        assertTrue(retried)
    }
}
