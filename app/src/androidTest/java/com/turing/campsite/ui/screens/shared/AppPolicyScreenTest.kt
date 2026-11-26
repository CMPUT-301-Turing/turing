package com.turing.campsite.ui.screens.shared

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * AppPolicyScreen UI tests (US 01.11.01)
 *
 * Checks that the policy sections are shown and the back button works.
 */
class AppPolicyScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Tests that the heading and every policy section title are shown
     */
    @Test
    fun appPolicyScreen_showsAllSections() {
        composeTestRule.setContent {
            AppPolicyScreen(onBackClick = {})
        }

        composeTestRule.onNodeWithText("App policy and guidelines").assertIsDisplayed()

        appPolicySections.forEach { section ->
            composeTestRule.onNodeWithText(section.title)
                .performScrollTo()
                .assertIsDisplayed()
        }
    }

    /**
     * Tests that custom sections passed to the screen are shown
     */
    @Test
    fun appPolicyScreen_showsGivenSections() {
        composeTestRule.setContent {
            AppPolicyScreen(
                onBackClick = {},
                sections = listOf(PolicySection("Test title", "Test body"))
            )
        }

        composeTestRule.onNodeWithText("Test title").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test body").assertIsDisplayed()
    }

    /**
     * Tests the back button
     */
    @Test
    fun appPolicyScreen_backButtonClicks() {
        var backClicked = false

        composeTestRule.setContent {
            AppPolicyScreen(onBackClick = { backClicked = true })
        }

        composeTestRule.onNodeWithContentDescription("Back to profile").performClick()

        assertTrue(backClicked)
    }
}
