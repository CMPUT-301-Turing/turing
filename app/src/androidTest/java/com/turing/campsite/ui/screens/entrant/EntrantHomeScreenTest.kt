package com.turing.campsite.ui.screens.entrant

import org.junit.Rule
import org.junit.Test
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

}