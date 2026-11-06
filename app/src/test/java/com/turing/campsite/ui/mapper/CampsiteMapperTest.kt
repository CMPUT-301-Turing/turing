package com.turing.campsite.ui.mapper

import com.turing.campsite.data.model.Campsite
import com.turing.campsite.ui.components.CampsiteFormData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CampsiteMapperTest {
    private lateinit var pinewood: Campsite
    private lateinit var form: CampsiteFormData

    @Before
    fun setUp() {
        pinewood = Campsite(
            id = "c1",
            ownerId = "owner-1",
            name = "Pinewood Retreat",
            location = "Jasper, Alberta",
            description = "Quiet forest site with fire pits.",
            maxOccupancy = 8,
            tags = listOf("Woodland", "Pet Friendly"),
            ratingAverage = 4.75,
            ratingCount = 4
        )
        form = CampsiteFormData(
            name = " Lakeside Meadow ",
            maximumOccupancy = "6",
            description = "Open meadow next to the lake.",
            location = "Canmore, Alberta",
            mapLocation = "Canmore",
            tags = listOf("Lake"),
            campsiteRules = "Quiet after 10 PM.",
            cancellationPolicy = "Full refund up to 7 days before.",
            confirmationRequired = false,
            isActive = false
        )
    }

    @Test
    fun toDetailsPreview_campsite_copiesDetails() {
        val details = pinewood.toDetailsPreview()

        assertEquals("Pinewood Retreat", details.name)
        assertEquals("Woodland · Pet Friendly", details.type)
        assertEquals("Max 8 people", details.maximumOccupancy)
        assertEquals("Quiet forest site with fire pits.", details.description)
        assertEquals("4.8", details.rating)
        assertEquals(4, details.reviewCount)
        assertEquals(0, details.pitchCount) // not stored, so the screen hides it
    }

    @Test
    fun toCampsite_filledForm_copiesAndTrimsFields() {
        val campsite = form.toCampsite("owner-1")

        assertEquals("owner-1", campsite.ownerId)
        assertEquals("Lakeside Meadow", campsite.name) // spaces around the name removed
        assertEquals(6, campsite.maxOccupancy)
        assertEquals("Canmore, Alberta", campsite.location)
        assertEquals("Canmore", campsite.mapLocation)
        assertEquals(listOf("Lake"), campsite.tags)
        assertEquals("Quiet after 10 PM.", campsite.rulesText)
        assertEquals("Full refund up to 7 days before.", campsite.cancellationPolicy)
        assertFalse(campsite.confirmationRequired)
        assertFalse(campsite.active)
    }

    @Test
    fun toCampsite_newCampsite_startsUnratedWithNoId() {
        val campsite = form.toCampsite("owner-1")

        assertEquals("", campsite.id) // the repository fills this in when saving
        assertEquals(0, campsite.ratingCount)
        assertTrue(campsite.imageUrl.isEmpty())
    }

    @Test
    fun toCampsite_occupancyNotANumber_becomesZero() {
        val campsite = form.copy(maximumOccupancy = "many").toCampsite("owner-1")

        assertEquals(0, campsite.maxOccupancy)
    }
}
