package com.turing.campsite.data.model

import com.google.firebase.Timestamp
import org.junit.Assert.*
import org.junit.Test

/**
 * Checks campsite defaults, constructor values, and independent in-memory field edits.
 *
 * Exercises only model behavior; no persistence, UI, or server authorization is tested.
 */
class CampsiteModelTest {
    private val fixed = Timestamp(1_000L, 123)
    private val later = Timestamp(2_000L, 456)

    /** Verifies that a new campsite has empty details, zero capacity and ratings, and the supplied timestamp. */
    @Test fun defaultsRepresentAnUnconfiguredUnratedCampsite() {
        val subject = Campsite(createdAt = fixed)
        assertEquals("", subject.id)
        assertEquals("", subject.ownerId)
        assertEquals("", subject.name)
        assertEquals("", subject.location)
        assertEquals("", subject.description)
        assertEquals(0, subject.maxOccupancy)
        assertTrue(subject.tags.isEmpty())
        assertEquals("", subject.imageUrl)
        assertEquals(0.0, subject.ratingAverage, 0.0)
        assertEquals(0, subject.ratingCount)
        assertEquals("", subject.rulesText)
        assertEquals(fixed, subject.createdAt)
    }

    /** Verifies that construction retains every supplied campsite detail and rating value. */
    @Test fun constructorRetainsFullCampsiteDetails() {
        val subject = Campsite("site-1", "owner-1", "Lake", "Alberta", "Tent site", 4,
            listOf("lake", "pets"), "https://example.invalid/site.jpg", 4.5, 2, "Quiet after ten", fixed)
        assertEquals("site-1", subject.id)
        assertEquals("owner-1", subject.ownerId)
        assertEquals("Lake", subject.name)
        assertEquals("Alberta", subject.location)
        assertEquals("Tent site", subject.description)
        assertEquals(4, subject.maxOccupancy)
        assertEquals(listOf("lake", "pets"), subject.tags)
        assertEquals("https://example.invalid/site.jpg", subject.imageUrl)
        assertEquals(4.5, subject.ratingAverage, 0.0)
        assertEquals(2, subject.ratingCount)
        assertEquals("Quiet after ten", subject.rulesText)
        assertEquals(fixed, subject.createdAt)
    }

    /** Verifies that editing descriptive fields leaves identity, ratings, and creation time unchanged. */
    @Test fun ownerEditsRetainIdentityAndStoredRatings() {
        val subject = Campsite(id = "site-1", ownerId = "owner-1", ratingAverage = 4.5, ratingCount = 2, createdAt = fixed)
        subject.name = "Forest"
        subject.location = "Edmonton"
        subject.description = "Cabin"
        subject.maxOccupancy = 6
        subject.rulesText = "No fires"
        assertEquals("Forest", subject.name)
        assertEquals("Edmonton", subject.location)
        assertEquals("Cabin", subject.description)
        assertEquals(6, subject.maxOccupancy)
        assertEquals("No fires", subject.rulesText)
        assertEquals("site-1", subject.id)
        assertEquals("owner-1", subject.ownerId)
        assertEquals(4.5, subject.ratingAverage, 0.0)
        assertEquals(2, subject.ratingCount)
        assertEquals(fixed, subject.createdAt)
    }

    /** Verifies that tags and an image URL can be assigned and then cleared. */
    @Test fun optionalTagsAndImageCanBeAddedAndCleared() {
        val subject = Campsite(createdAt = fixed)
        subject.tags = listOf("water", "pets")
        subject.imageUrl = "https://example.invalid/camp.png"
        assertEquals(listOf("water", "pets"), subject.tags)
        assertEquals("https://example.invalid/camp.png", subject.imageUrl)
        subject.tags = emptyList()
        subject.imageUrl = ""
        assertTrue(subject.tags.isEmpty())
        assertEquals("", subject.imageUrl)
    }

    /** Verifies that changing rating aggregates leaves capacity and tags unchanged. */
    @Test fun updatingRatingAggregateDoesNotChangeCapacityOrTags() {
        val subject = Campsite(maxOccupancy = 4, tags = listOf("lake"), createdAt = fixed)
        subject.ratingAverage = 3.5
        subject.ratingCount = 2
        assertEquals(3.5, subject.ratingAverage, 0.0)
        assertEquals(2, subject.ratingCount)
        assertEquals(4, subject.maxOccupancy)
        assertEquals(listOf("lake"), subject.tags)
    }

}
