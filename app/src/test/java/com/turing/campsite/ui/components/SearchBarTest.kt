package com.turing.campsite.ui.components

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchBarTest {

    @Test
    fun matchesSearch_emptyQuery_matchesEverything() {
        assertTrue(matchesSearch("", "Pinewood Retreat", "Jasper, Alberta"))
        assertTrue(matchesSearch("   ", "Pinewood Retreat")) // only spaces counts as empty
    }

    @Test
    fun matchesSearch_partOfName_matches() {
        assertTrue(matchesSearch("pine", "Pinewood Retreat", "Jasper, Alberta"))
    }

    @Test
    fun matchesSearch_differentCase_matches() {
        assertTrue(matchesSearch("JASPER", "Pinewood Retreat", "Jasper, Alberta"))
    }

    @Test
    fun matchesSearch_spacesAroundQuery_ignored() {
        assertTrue(matchesSearch("  jasper ", "Pinewood Retreat", "Jasper, Alberta"))
    }

    @Test
    fun matchesSearch_secondField_matches() {
        assertTrue(matchesSearch("alberta", "Pinewood Retreat", "Jasper, Alberta"))
    }

    @Test
    fun matchesSearch_noFieldContainsQuery_doesNotMatch() {
        assertFalse(matchesSearch("lake", "Pinewood Retreat", "Jasper, Alberta"))
    }

    @Test
    fun matchesSearch_noFields_doesNotMatch() {
        assertFalse(matchesSearch("pine"))
    }
}
