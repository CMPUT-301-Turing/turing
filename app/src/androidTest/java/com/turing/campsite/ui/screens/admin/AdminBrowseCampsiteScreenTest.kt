/*
 * AI Assistance Disclosure
 *
 * Company: OpenAI
 * Tool: ChatGPT (GPT-6)
 * Date: 2026-11-24
 * Subject: Admin campsite browsing UI tests, search functionality,
 *          loading and empty states, and Jetpack Compose testing.
 *
 * Scope: AI-assisted implementation and review of UI tests.
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
import com.turing.campsite.data.model.Campsite
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for AdminBrowseCampsiteScreen.
 *
 * Verifies navigation, campsite rendering, search filtering,
 * empty states, and loading behavior.
 */
class AdminBrowseCampsiteScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Sample data provided directly to the screen for testing.
    private val testCampsites = listOf(
        Campsite(
            id = "1",
            name = "Pinewood Retreat",
            location = "New Forest",
            maxOccupancy = 6
        ),
        Campsite(
            id = "2",
            name = "Oak Hollow",
            location = "Brecon",
            maxOccupancy = 4
        )
    )

    /**
     * Renders the screen without connecting to Firestore.
     */
    private fun setScreen(
        campsites: List<Campsite> = emptyList(),
        isLoading: Boolean = false,
        onBackClick: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            AdminBrowseCampsiteScreen(
                onBackClick = onBackClick,
                campsites = campsites,
                isLoading = isLoading
            )
        }
    }

    /**
     * Tests that the back button invokes its callback.
     */
    @Test
    fun adminBrowseCampsiteScreen_backButtonClicks() {
        var clickedBack = false

        setScreen(onBackClick = { clickedBack = true })

        composeTestRule
            .onNodeWithContentDescription("Back to admin home")
            .performClick()

        assertTrue(clickedBack)
    }

    /**
     * Tests that the screen title is displayed.
     */
    @Test
    fun adminBrowseCampsiteScreen_rendersTitle() {
        setScreen()

        composeTestRule
            .onNodeWithText("Browse Campsites")
            .assertIsDisplayed()
    }

    /**
     * Tests that the supplied campsite list is displayed.
     */
    @Test
    fun adminBrowseCampsiteScreen_displaysCampsites() {
        setScreen(campsites = testCampsites)

        composeTestRule
            .onNodeWithText("Pinewood Retreat")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Oak Hollow")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("2 campsites")
            .assertIsDisplayed()
    }

    /**
     * Tests filtering by campsite name.
     */
    @Test
    fun adminBrowseCampsiteScreen_searchesByName() {
        setScreen(campsites = testCampsites)

        composeTestRule
            .onNode(hasSetTextAction())
            .performTextInput("Pinewood")

        composeTestRule
            .onNodeWithText("Pinewood Retreat")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Oak Hollow")
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText("1 campsites")
            .assertIsDisplayed()
    }

    /**
     * Tests filtering by campsite location.
     */
    @Test
    fun adminBrowseCampsiteScreen_searchesByLocation() {
        setScreen(campsites = testCampsites)

        composeTestRule
            .onNode(hasSetTextAction())
            .performTextInput("Brecon")

        composeTestRule
            .onNodeWithText("Oak Hollow")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Pinewood Retreat")
            .assertDoesNotExist()
    }

    /**
     * Tests case-insensitive searching.
     */
    @Test
    fun adminBrowseCampsiteScreen_searchIsCaseInsensitive() {
        setScreen(campsites = testCampsites)

        composeTestRule
            .onNode(hasSetTextAction())
            .performTextInput("pInEwOoD")

        composeTestRule
            .onNodeWithText("Pinewood Retreat")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Oak Hollow")
            .assertDoesNotExist()
    }

    /**
     * Tests that clearing search restores all campsites.
     */
    @Test
    fun adminBrowseCampsiteScreen_clearingSearchRestoresList() {
        setScreen(campsites = testCampsites)

        composeTestRule
            .onNode(hasSetTextAction())
            .performTextInput("Pinewood")

        composeTestRule
            .onNode(hasSetTextAction())
            .performTextClearance()

        composeTestRule
            .onNodeWithText("Pinewood Retreat")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Oak Hollow")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("2 campsites")
            .assertIsDisplayed()
    }

    /**
     * Tests the empty state when no campsites exist.
     */
    @Test
    fun adminBrowseCampsiteScreen_showsNoCampsitesMessage() {
        setScreen(campsites = emptyList())

        composeTestRule
            .onNodeWithText("No campsites registered yet.")
            .assertIsDisplayed()
    }

    /**
     * Tests the empty state when search has no matches.
     */
    @Test
    fun adminBrowseCampsiteScreen_showsNoSearchResultsMessage() {
        setScreen(campsites = testCampsites)

        composeTestRule
            .onNode(hasSetTextAction())
            .performTextInput("Nonexistent Campsite")

        composeTestRule
            .onNodeWithText("No campsites match your search.")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("No campsites registered yet.")
            .assertDoesNotExist()
    }

    /**
     * Tests loading behavior before campsite data is available.
     */
    @Test
    fun adminBrowseCampsiteScreen_showsLoadingIndicator() {
        setScreen(
            campsites = emptyList(),
            isLoading = true
        )

        composeTestRule
            .onNode(
                hasProgressBarRangeInfo(
                    ProgressBarRangeInfo.Indeterminate
                )
            )
            .assertExists()

        composeTestRule
            .onNodeWithText("No campsites registered yet.")
            .assertDoesNotExist()
    }
}