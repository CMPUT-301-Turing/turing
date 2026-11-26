package com.turing.campsite.ui.screens.shared

import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

/**
 * UserEditProfileScreen UI tests (US 01.07.03)
 *
 * Tests the buttons, the saved details showing up in the fields,
 * and that save only goes through when the fields are valid.
 */
class UserEditProfileScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Shows the screen with Alice's details
     */
    private fun setScreen(
        isSaving: Boolean = false,
        errorMessage: String? = null,
        onSaveClick: (String, String, String) -> Unit = { _, _, _ -> },
        onBackClick: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            UserEditProfileScreen(
                initialName = "Alice",
                initialEmail = "alice@example.com",
                initialPhone = "780-555-0100",
                isSaving = isSaving,
                errorMessage = errorMessage,
                onSaveClick = onSaveClick,
                onBackClick = onBackClick
            )
        }
    }

    /**
     * Tests back button
     */
    @Test
    fun userEditProfileScreen_backButtonClicks() {
        var backClicked = false
        setScreen(onBackClick = { backClicked = true })

        // find back to profile button and click it
        composeTestRule.onNodeWithContentDescription("Back to profile").performClick()

        // check if really pressed
        assertTrue(backClicked)
    }

    /**
     * Tests cancel button
     */
    @Test
    fun userEditProfileScreen_cancelButtonClicks() {
        var cancelClicked = false
        setScreen(onBackClick = { cancelClicked = true })

        composeTestRule.onNodeWithText("Cancel").performClick()

        assertTrue(cancelClicked)
    }

    /**
     * Tests that the saved details are filled in
     */
    @Test
    fun userEditProfileScreen_showsSavedDetails() {
        setScreen()

        composeTestRule.onNodeWithText("Alice").assertExists()
        composeTestRule.onNodeWithText("alice@example.com").assertExists()
        composeTestRule.onNodeWithText("780-555-0100").assertExists()
    }

    /**
     * Tests that save sends the edited details
     */
    @Test
    fun userEditProfileScreen_saveSendsEditedDetails() {
        var saved: Triple<String, String, String>? = null
        setScreen(onSaveClick = { name, email, phone -> saved = Triple(name, email, phone) })

        composeTestRule.onNodeWithText("Alice").performTextReplacement("Alice B ")
        composeTestRule.onNodeWithText("Save changes").performClick()

        // name gets trimmed
        assertEquals(Triple("Alice B", "alice@example.com", "780-555-0100"), saved)
    }

    /**
     * Tests that an empty name shows an error and does not save
     */
    @Test
    fun userEditProfileScreen_emptyNameDoesNotSave() {
        var saveClicked = false
        setScreen(onSaveClick = { _, _, _ -> saveClicked = true })

        composeTestRule.onNodeWithText("Alice").performTextReplacement("")
        composeTestRule.onNodeWithText("Save changes").performClick()

        composeTestRule.onNodeWithText("Name is required").assertExists()
        assertFalse(saveClicked)
    }

    /**
     * Tests that a bad email shows an error and does not save
     */
    @Test
    fun userEditProfileScreen_badEmailDoesNotSave() {
        var saveClicked = false
        setScreen(onSaveClick = { _, _, _ -> saveClicked = true })

        composeTestRule.onNodeWithText("alice@example.com").performTextReplacement("not an email")
        composeTestRule.onNodeWithText("Save changes").performClick()

        composeTestRule.onNodeWithText("Enter a valid email").assertExists()
        assertFalse(saveClicked)
    }

    /**
     * Tests that the save button is disabled while saving
     */
    @Test
    fun userEditProfileScreen_savingDisablesButton() {
        setScreen(isSaving = true)

        composeTestRule.onNodeWithText("Saving...").assertIsNotEnabled()
    }

    /**
     * Tests that a save error is shown
     */
    @Test
    fun userEditProfileScreen_showsErrorMessage() {
        setScreen(errorMessage = "no internet")

        composeTestRule.onNodeWithText("Something went wrong: no internet").assertExists()
    }
}
