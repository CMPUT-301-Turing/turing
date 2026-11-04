package com.turing.campsite.ui.screens.shared

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun retryButtonInvokesCallback() {
        var retryCount = 0

        composeTestRule.setContent {
            val navController = rememberNavController()

            LoginScreen(
                navController = navController,
                loginState = LoginUiState.Error(
                    "Unable to identify device"
                ),
                onRetry = {
                    retryCount++
                }
            )
        }

        composeTestRule
            .onNodeWithTag("retryLoginButton")
            .assertExists()
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(1, retryCount)
        }
    }
}