package com.turing.campsite.ui.screens.entrant

import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals





/**
 * EntrantHomeScreen UI tests
 *
 * Tests pretty much all buttons on the screen
 */
class EntrantHomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Tests if screen load and show header text
     */
    @Test
    fun entrantHomeScreen_rendersHeader() {

        composeTestRule.setContent {

            EntrantHomeScreen(onCampsiteClick = {})
        }

        // check main title
        composeTestRule.onNodeWithText("Campsites").assertIsDisplayed()

    }



    /**
     * Tests clicking a campsite card
     * we need to click hardcoded name because card has no id
     */
    @Test
    fun entrantHomeScreen_campsiteCardClickPassesName() {

        var clickedName = ""


        composeTestRule.setContent {

            EntrantHomeScreen(
                onCampsiteClick = { name -> clickedName = name }
            )

        }


        // click the first card
        composeTestRule.onNodeWithText("Pinewood Retreat").performClick()


        // check if correct name passed
        assertEquals("Pinewood Retreat", clickedName)

    }

}