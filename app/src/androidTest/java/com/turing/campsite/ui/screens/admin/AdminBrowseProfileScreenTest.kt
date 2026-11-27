/*
 * AI Assistance Disclosure
 *
 * Company: OpenAI
 * Tool: ChatGPT (GPT-6)
 *
 * Subjects:
 * 2026-11-25: Admin profile browsing, search, and UI state tests (US 03.05.01).
 *
 * Scope: AI-assisted implementation, debugging, and code review.
 */

package com.turing.campsite.ui.screens.admin

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.turing.campsite.data.model.User
import com.turing.campsite.data.model.UserRole
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Compose UI tests for admin profile browsing.
 *
 * Uses supplied users rather than Firebase to verify the screen's presentation,
 * search behavior, loading indicator, and empty states.
 */
class AdminBrowseProfilesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testProfiles = listOf(
        User(
            id = "u1",
            name = "Priya Shah",
            email = "priya@example.com",
            roles = listOf(UserRole.OWNER)
        ),
        User(
            id = "u2",
            name = "Morgan Lee",
            email = "morgan@university.ca",
            roles = listOf(UserRole.ENTRANT, UserRole.ADMIN)
        )
    )

    /** Renders the screen with test-controlled data, without accessing Firebase. */
    private fun setScreen(
        profiles: List<User> = emptyList(),
        isLoading: Boolean = false,
        onBackClick: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            AdminBrowseProfilesScreen(
                onBackClick = onBackClick,
                profiles = profiles,
                isLoading = isLoading
            )
        }
    }

    @Test
    fun adminBrowseProfilesScreen_backButtonClicks() {
        var clickedBack = false
        setScreen(onBackClick = { clickedBack = true })

        composeTestRule.onNodeWithContentDescription("Back to admin home").performClick()

        assertTrue(clickedBack)
    }

    @Test
    fun adminBrowseProfilesScreen_showsTitleAndSearchField() {
        setScreen()

        composeTestRule.onNodeWithText("Browse Profiles").assertIsDisplayed()
        composeTestRule.onNodeWithText("Search by name or email").assertIsDisplayed()
        composeTestRule.onNode(hasSetTextAction()).assertExists()
    }

    @Test
    fun adminBrowseProfilesScreen_displaysProvidedProfilesAndRoles() {
        setScreen(profiles = testProfiles)

        composeTestRule.onNodeWithText("Priya Shah").assertIsDisplayed()
        composeTestRule.onNodeWithText("priya@example.com").assertIsDisplayed()
        composeTestRule.onNodeWithText("Owner").assertIsDisplayed()
        composeTestRule.onNodeWithText("Morgan Lee").assertIsDisplayed()
        composeTestRule.onNodeWithText("morgan@university.ca").assertIsDisplayed()
        composeTestRule.onNodeWithText("Entrant, Admin").assertIsDisplayed()
        composeTestRule.onNodeWithText("2 profiles").assertIsDisplayed()
    }

    @Test
    fun adminBrowseProfilesScreen_searchesByNameIgnoringCaseAndWhitespace() {
        setScreen(profiles = testProfiles)

        composeTestRule.onNode(hasSetTextAction()).performTextInput("  pRiYa  ")

        composeTestRule.onNodeWithText("Priya Shah").assertIsDisplayed()
        composeTestRule.onNodeWithText("Morgan Lee").assertDoesNotExist()
        composeTestRule.onNodeWithText("1 profiles").assertIsDisplayed()
    }

    @Test
    fun adminBrowseProfilesScreen_searchesByEmailIgnoringCase() {
        setScreen(profiles = testProfiles)

        composeTestRule.onNode(hasSetTextAction()).performTextInput("UNIVERSITY.CA")

        composeTestRule.onNodeWithText("Morgan Lee").assertIsDisplayed()
        composeTestRule.onNodeWithText("Priya Shah").assertDoesNotExist()
        composeTestRule.onNodeWithText("1 profiles").assertIsDisplayed()
    }

    @Test
    fun adminBrowseProfilesScreen_clearingSearchRestoresAllProfiles() {
        setScreen(profiles = testProfiles)
        composeTestRule.onNode(hasSetTextAction()).performTextInput("Priya")

        composeTestRule.onNode(hasSetTextAction()).performTextClearance()

        composeTestRule.onNodeWithText("Priya Shah").assertIsDisplayed()
        composeTestRule.onNodeWithText("Morgan Lee").assertIsDisplayed()
        composeTestRule.onNodeWithText("2 profiles").assertIsDisplayed()
    }

    @Test
    fun adminBrowseProfilesScreen_emptyDatabaseShowsMessageNotSampleProfiles() {
        setScreen(profiles = emptyList())

        composeTestRule.onNodeWithText("No profiles registered yet.").assertIsDisplayed()
        composeTestRule.onNodeWithText("0 profiles").assertIsDisplayed()
        composeTestRule.onNodeWithText("Priya Shah").assertDoesNotExist()
    }

    @Test
    fun adminBrowseProfilesScreen_noSearchMatchesShowsDistinctMessage() {
        setScreen(profiles = testProfiles)

        composeTestRule.onNode(hasSetTextAction()).performTextInput("nobody@example.com")

        composeTestRule.onNodeWithText("No profiles match your search.").assertIsDisplayed()
        composeTestRule.onNodeWithText("No profiles registered yet.").assertDoesNotExist()
        composeTestRule.onNodeWithText("0 profiles").assertIsDisplayed()
        composeTestRule.onNodeWithText("Priya Shah").assertDoesNotExist()
    }

    @Test
    fun adminBrowseProfilesScreen_loadingShowsProgressInsteadOfEmptyState() {
        setScreen(profiles = emptyList(), isLoading = true)

        composeTestRule.onNode(
            hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)
        ).assertExists()
        composeTestRule.onNodeWithText("No profiles registered yet.").assertDoesNotExist()
    }

    @Test
    fun adminBrowseProfilesScreen_blankOptionalFieldsShowFallbacks() {
        setScreen(
            profiles = listOf(
                User(id = "u3", name = "", email = "", roles = emptyList())
            )
        )

        composeTestRule.onNodeWithText("Unnamed profile").assertIsDisplayed()
        composeTestRule.onNodeWithText("No email provided").assertIsDisplayed()
        composeTestRule.onNodeWithText("No roles assigned").assertIsDisplayed()
    }
}