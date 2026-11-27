/*
 * AI Assistance Disclosure
 *
 * Company: OpenAI
 * Tool: ChatGPT (GPT-6)
 *
 * Subjects:
 * 2026-11-24: Campsite browsing, search, and UI state tests.
 * 2026-11-25: Campsite deletion confirmation tests.
 *
 * Scope: AI-assisted implementation, debugging, and code review.
 */

package com.turing.campsite.ui.screens.admin

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.turing.campsite.data.model.Campsite
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for AdminBrowseCampsiteScreen, including search, loading states,
 * and confirmation before invoking the deletion callback.
 */
class AdminBrowseCampsiteScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

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

    /** Renders the screen without connecting to Firestore. */
    private fun setScreen(
        campsites: List<Campsite> = emptyList(),
        isLoading: Boolean = false,
        onBackClick: () -> Unit = {},
        onDeleteCampsite: ((String) -> Unit)? = null
    ) {
        composeTestRule.setContent {
            AdminBrowseCampsiteScreen(
                onBackClick = onBackClick,
                campsites = campsites,
                isLoading = isLoading,
                onDeleteCampsite = onDeleteCampsite
            )
        }
    }

    @Test
    fun adminBrowseCampsiteScreen_backButtonClicks() {
        var clickedBack = false
        setScreen(onBackClick = { clickedBack = true })

        composeTestRule.onNodeWithContentDescription("Back to admin home").performClick()

        assertTrue(clickedBack)
    }

    @Test
    fun adminBrowseCampsiteScreen_rendersTitle() {
        setScreen()
        composeTestRule.onNodeWithText("Browse Campsites").assertIsDisplayed()
    }

    @Test
    fun adminBrowseCampsiteScreen_displaysCampsites() {
        setScreen(campsites = testCampsites)

        composeTestRule.onNodeWithText("Pinewood Retreat").assertIsDisplayed()
        composeTestRule.onNodeWithText("Oak Hollow").assertIsDisplayed()
        composeTestRule.onNodeWithText("2 campsites").assertIsDisplayed()
    }

    @Test
    fun adminBrowseCampsiteScreen_searchesByName() {
        setScreen(campsites = testCampsites)
        composeTestRule.onNode(hasSetTextAction()).performTextInput("Pinewood")

        composeTestRule.onNodeWithText("Pinewood Retreat").assertIsDisplayed()
        composeTestRule.onNodeWithText("Oak Hollow").assertDoesNotExist()
        composeTestRule.onNodeWithText("1 campsites").assertIsDisplayed()
    }

    @Test
    fun adminBrowseCampsiteScreen_searchesByLocation() {
        setScreen(campsites = testCampsites)
        composeTestRule.onNode(hasSetTextAction()).performTextInput("Brecon")

        composeTestRule.onNodeWithText("Oak Hollow").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pinewood Retreat").assertDoesNotExist()
    }

    @Test
    fun adminBrowseCampsiteScreen_searchIsCaseInsensitive() {
        setScreen(campsites = testCampsites)
        composeTestRule.onNode(hasSetTextAction()).performTextInput("pInEwOoD")

        composeTestRule.onNodeWithText("Pinewood Retreat").assertIsDisplayed()
        composeTestRule.onNodeWithText("Oak Hollow").assertDoesNotExist()
    }

    @Test
    fun adminBrowseCampsiteScreen_clearingSearchRestoresList() {
        setScreen(campsites = testCampsites)
        composeTestRule.onNode(hasSetTextAction()).performTextInput("Pinewood")
        composeTestRule.onNode(hasSetTextAction()).performTextClearance()

        composeTestRule.onNodeWithText("Pinewood Retreat").assertIsDisplayed()
        composeTestRule.onNodeWithText("Oak Hollow").assertIsDisplayed()
        composeTestRule.onNodeWithText("2 campsites").assertIsDisplayed()
    }

    @Test
    fun adminBrowseCampsiteScreen_showsNoCampsitesMessage() {
        setScreen(campsites = emptyList())
        composeTestRule.onNodeWithText("No campsites registered yet.").assertIsDisplayed()
    }

    @Test
    fun adminBrowseCampsiteScreen_showsNoSearchResultsMessage() {
        setScreen(campsites = testCampsites)
        composeTestRule.onNode(hasSetTextAction()).performTextInput("Nonexistent Campsite")

        composeTestRule.onNodeWithText("No campsites match your search.").assertIsDisplayed()
        composeTestRule.onNodeWithText("No campsites registered yet.").assertDoesNotExist()
    }

    @Test
    fun adminBrowseCampsiteScreen_showsLoadingIndicator() {
        setScreen(campsites = emptyList(), isLoading = true)

        composeTestRule.onNode(
            hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)
        ).assertExists()
        composeTestRule.onNodeWithText("No campsites registered yet.").assertDoesNotExist()
    }

    @Test
    fun delete_clickingIcon_opensConfirmationWithoutDeleting() {
        var deletedId: String? = null
        setScreen(campsites = testCampsites, onDeleteCampsite = { deletedId = it })

        composeTestRule.onAllNodesWithContentDescription("Delete campsite")[0].performClick()

        composeTestRule.onNodeWithText("Delete campsite?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Are you sure you want to delete \"Pinewood Retreat\"?", substring = true)
            .assertIsDisplayed()
        assertEquals(null, deletedId)
    }

    @Test
    fun delete_cancel_closesDialogWithoutCallingCallback() {
        var called = false
        setScreen(campsites = testCampsites, onDeleteCampsite = { called = true })
        composeTestRule.onAllNodesWithContentDescription("Delete campsite")[0].performClick()

        composeTestRule.onNodeWithText("Cancel").performClick()

        composeTestRule.onNodeWithText("Delete campsite?").assertDoesNotExist()
        assertFalse(called)
    }

    @Test
    fun delete_confirm_passesCorrectCampsiteIdOnce() {
        val deletedIds = mutableListOf<String>()
        setScreen(campsites = testCampsites, onDeleteCampsite = { deletedIds.add(it) })
        composeTestRule.onAllNodesWithContentDescription("Delete campsite")[1].performClick()

        composeTestRule.onNodeWithText("Delete").performClick()

        assertEquals(listOf("2"), deletedIds)
        composeTestRule.onNodeWithText("Delete campsite?").assertDoesNotExist()
    }

    @Test
    fun delete_searchFilteredList_stillDeletesMatchingCampsite() {
        val deletedIds = mutableListOf<String>()
        setScreen(campsites = testCampsites, onDeleteCampsite = { deletedIds.add(it) })
        composeTestRule.onNode(hasSetTextAction()).performTextInput("Oak")
        composeTestRule.onNodeWithContentDescription("Delete campsite").performClick()
        composeTestRule.onNodeWithText("Delete").performClick()

        assertEquals(listOf("2"), deletedIds)
    }
}