package com.turing.campsite.ui.screens.entrant

import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick

/**
 * EntrantCampsiteListScreen UI tests
 *
 * Tests the screen loads and the dropdown menu works to change sections
 */
class EntrantCampsiteListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Tests if screen loads with header text
     */
    @Test
    fun entrantCampsiteListScreen_rendersHeader()
    {

        composeTestRule.setContent {

            EntrantCampsiteListContent(allReservations = emptyList())
        }

        // check title
        composeTestRule.onNodeWithText("Campsite lists").assertIsDisplayed()
    }




    /**
     * Tests if clicking dropdown changes the section
     */
    @Test
    fun entrantCampsiteListScreen_dropdownChangesSection()
    {


        composeTestRule.setContent {
            EntrantCampsiteListContent(allReservations = emptyList())
        }


        // click dropdown and start as active reservation
        composeTestRule.onNodeWithText("Active reservations").performClick()



        // click for inactive
        composeTestRule.onNodeWithText("Inactive reservations").performClick()

        // check if inactive displayed
        composeTestRule.onNodeWithText("Inactive reservations").assertIsDisplayed()
    }
}