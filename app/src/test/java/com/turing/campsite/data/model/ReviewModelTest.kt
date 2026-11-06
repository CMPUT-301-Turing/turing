package com.turing.campsite.data.model

import com.google.firebase.Timestamp
import org.junit.Assert.*
import org.junit.Test

/**
 * Checks review defaults, rating and comment retention, and in-memory edits within the documented rating range.
 *
 * Exercises only model behavior; no persistence, UI, or server authorization is tested.
 */
class ReviewModelTest {
    private val fixed = Timestamp(1_000L, 123)
    private val later = Timestamp(2_000L, 456)

    /** Verifies empty identifiers and comment, a zero rating, and the supplied creation time. */
    @Test fun defaultsHaveNoRatingOrOptionalComment() {
        val subject = Review(createdAt = fixed)
        assertEquals("", subject.id)
        assertEquals("", subject.entrantId)
        assertEquals(0, subject.rating)
        assertEquals("", subject.comment)
        assertEquals(fixed, subject.createdAt)
    }

    /** Verifies that each rating from one through five retains its supplied identity, author, comment, and timestamp. */
    @Test fun eachDocumentedStarRatingIsStoredWithItsAuthor() {
        for (rating in 1..5) {
            val subject = Review("review-1", "entrant-1", rating, "Great stay", fixed)
            assertEquals("review-1", subject.id)
            assertEquals("entrant-1", subject.entrantId)
            assertEquals(rating, subject.rating)
            assertEquals("Great stay", subject.comment)
            assertEquals(fixed, subject.createdAt)
        }
    }

    /** Verifies that an omitted comment is empty and can be assigned and cleared without changing the rating. */
    @Test fun commentCanBeOmittedAndCleared() {
        val subject = Review(rating = 5, createdAt = fixed)
        assertEquals("", subject.comment)
        subject.comment = "Nice campsite"
        assertEquals("Nice campsite", subject.comment)
        subject.comment = ""
        assertEquals("", subject.comment)
        assertEquals(5, subject.rating)
    }

    /** Verifies that editing a rating preserves review identity, author, comment, and creation time. */
    @Test fun ratingEditPreservesAuthorCommentAndTimestamp() {
        val subject = Review("review-1", "entrant-1", 2, "Updated after stay", fixed)
        subject.rating = 4
        assertEquals(4, subject.rating)
        assertEquals("review-1", subject.id)
        assertEquals("entrant-1", subject.entrantId)
        assertEquals("Updated after stay", subject.comment)
        assertEquals(fixed, subject.createdAt)
    }

    /** Verifies that review comments preserve Unicode and line breaks exactly. */
    @Test fun reviewTextRetainsUnicodeAndLineBreaks() {
        val text = "Café nearby 🌲\nWould return."
        assertEquals(text, Review(comment = text, createdAt = fixed).comment)
    }

}
