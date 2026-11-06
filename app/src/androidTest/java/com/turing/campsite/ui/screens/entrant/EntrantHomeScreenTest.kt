package com.turing.campsite.ui.screens.entrant

import org.junit.Rule
import org.junit.Test
import com.turing.campsite.ui.components.SEARCH_FIELD_TAG
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import com.turing.campsite.data.model.Campsite





/**
 * EntrantHomeScreen UI tests
 *
 * Tests pretty much all buttons on the screen
 */
class EntrantHomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // one campsite to show, so the test does not need the database
    private val pinewood = Campsite(
        id = "pinewood-retreat",
        name = "Pinewood Retreat",
        location = "Jasper, Alberta"
    )

    /**
     * Tests if screen load and show header text
     */
    @Test
    fun entrantHomeScreen_rendersHeader() {

        composeTestRule.setContent {

            EntrantHomeContent(
                activeCampsites = listOf(pinewood),
                onCampsiteClick = {}
            )
        }

        // check main title
        composeTestRule.onNodeWithText("Campsites").assertIsDisplayed()

    }



    /**
     * Tests clicking a campsite card
     * the card is found by its name and should pass the campsite id
     */
    @Test
    fun entrantHomeScreen_campsiteCardClickPassesId() {

        var clickedId = ""


        composeTestRule.setContent {

            EntrantHomeContent(
                activeCampsites = listOf(pinewood),
                onCampsiteClick = { id -> clickedId = id }
            )

        }


        // click the first card
        composeTestRule.onNodeWithText("Pinewood Retreat").performClick()


        // check if the id of that campsite was passed
        assertEquals("pinewood-retreat", clickedId)

    }


    /**
     * Tests that typing in the search box hides the campsites that do not match
     */
    @Test
    fun entrantHomeScreen_searchHidesOtherCampsites() {
        val heather = Campsite(
            id = "heather-ridge",
            name = "Heather Ridge",
            location = "Drumheller, Alberta"
        )

        composeTestRule.setContent {
            EntrantHomeContent(
                activeCampsites = listOf(pinewood, heather),
                onCampsiteClick = {}
            )
        }

        // both there before searching
        composeTestRule.onNodeWithText("Pinewood Retreat").assertIsDisplayed()
        composeTestRule.onNodeWithText("Heather Ridge").assertIsDisplayed()

        // search by part of the location
        composeTestRule.onNodeWithTag(SEARCH_FIELD_TAG).performTextInput("drum")

        // only Heather Ridge is left
        composeTestRule.onNodeWithText("Heather Ridge").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pinewood Retreat").assertDoesNotExist()
        composeTestRule.onNodeWithText("1 campsites").assertIsDisplayed()
    }
}