/*
 * AI Assistance Disclosure
 *
 * Company: OpenAI
 * Tool: ChatGPT (GPT-6)
 *
 * Subjects:
 * 2026-11-25: Batched campsite and review deletion tests.
 *
 * Scope: AI-assisted implementation, debugging, and code review.
 */

package com.turing.campsite.data.repository

import com.google.firebase.firestore.*
import com.turing.campsite.data.model.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.*

/**
 * Tests [CampsiteRepository] against mocked Firebase boundaries.
 *
 * Covers local mapping, branching, and error behavior; does not verify live
 * persistence or security rules.
 */
class CampsiteRepositoryTest : FirestoreTestFixture("campsites") {

    @Test fun getRestoresDocumentId() = runBlocking<Unit> {
        val value = Campsite(id = "stale")
        doReturn(completed(row(Campsite::class.java, value, "canonical"))).`when`(doc).get()
        val result = CampsiteRepository().get("id")
        assertSame(value, result)
        assertEquals("canonical", result!!.id)
    }

    @Test fun getMissingDocumentReturnsNull() = runBlocking<Unit> {
        doReturn(completed(row(Campsite::class.java, null))).`when`(doc).get()
        assertNull(CampsiteRepository().get("id"))
    }

    @Test fun getPropagatesFailure() = runBlocking<Unit> {
        val failure = IllegalStateException("read failed")
        doReturn(failed<DocumentSnapshot>(failure)).`when`(doc).get()
        try { CampsiteRepository().get("id"); fail("Expected read failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    @Test fun addAssignsAndReturnsGeneratedId() = runBlocking<Unit> {
        val value = Campsite(id = "previous")
        assertEquals("generated", CampsiteRepository().add(value))
        assertEquals("generated", value.id)
        verify(generatedDoc).set(value)
        verify(collection).document()
        verify(collection, never()).document(anyString())
        verify(doc, never()).set(anyPayload())
    }

    @Test fun addPropagatesWriteFailure() = runBlocking<Unit> {
        val value = Campsite()
        val failure = IllegalStateException("write failed")
        doReturn(failed<Void>(failure)).`when`(generatedDoc).set(value)
        try { CampsiteRepository().add(value); fail("Expected write failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    @Test fun listAllMapsIdsAndSkipsNullObjects() = runBlocking<Unit> {
        val value = Campsite(id = "stale")
        doReturn(completed(snapshot(row(Campsite::class.java, null), row(Campsite::class.java, value, "canonical")))).`when`(collection).get()
        val result = CampsiteRepository().listAll()
        assertEquals(listOf(value), result)
        assertEquals("canonical", result.single().id)
    }

    @Test fun listAllReturnsEmptyList() = runBlocking<Unit> {
        doReturn(completed(snapshot())).`when`(collection).get()
        assertTrue(CampsiteRepository().listAll().isEmpty())
    }

    @Test fun listAllPropagatesQueryFailure() = runBlocking<Unit> {
        val failure = IllegalStateException("query failed")
        doReturn(failed<QuerySnapshot>(failure)).`when`(collection).get()
        try { CampsiteRepository().listAll(); fail("Expected query failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    @Test fun listByOwnerMapsIdsAndSkipsNullObjects() = runBlocking<Unit> {
        val query = mock(Query::class.java)
        doReturn(query).`when`(collection).whereEqualTo("ownerId", "owner")
        val value = Campsite(id = "stale")
        doReturn(completed(snapshot(row(Campsite::class.java, null), row(Campsite::class.java, value, "canonical")))).`when`(query).get()
        val result = CampsiteRepository().listByOwner("owner")
        assertEquals(listOf(value), result)
        assertEquals("canonical", result.single().id)
    }

    @Test fun listByOwnerReturnsEmptyList() = runBlocking<Unit> {
        val query = mock(Query::class.java)
        doReturn(query).`when`(collection).whereEqualTo("ownerId", "owner")
        doReturn(completed(snapshot())).`when`(query).get()
        assertTrue(CampsiteRepository().listByOwner("owner").isEmpty())
    }

    @Test fun listByOwnerPropagatesQueryFailure() = runBlocking<Unit> {
        val query = mock(Query::class.java)
        doReturn(query).`when`(collection).whereEqualTo("ownerId", "owner")
        val failure = IllegalStateException("query failed")
        doReturn(failed<QuerySnapshot>(failure)).`when`(query).get()
        try { CampsiteRepository().listByOwner("owner"); fail("Expected query failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    @Test fun updateReplacesDocument() = runBlocking<Unit> {
        val value = Campsite(id = "id")
        CampsiteRepository().update(value)
        verify(doc).set(value)
    }

    @Test fun updatePropagatesFailure() = runBlocking<Unit> {
        val value = Campsite(id = "id")
        val failure = IllegalStateException("write failed")
        doReturn(failed<Void>(failure)).`when`(doc).set(value)
        try { CampsiteRepository().update(value); fail("Expected write failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    @Test fun deleteDeletesRequestedDocument() = runBlocking<Unit> {
        CampsiteRepository().delete("id")
        verify(doc).delete()
    }

    @Test fun deletePropagatesFailure() = runBlocking<Unit> {
        val failure = IllegalStateException("delete failed")
        doReturn(failed<Void>(failure)).`when`(doc).delete()
        try { CampsiteRepository().delete("id"); fail("Expected delete failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /** Stubs the current campsite document's reviews subcollection. */
    private fun reviews(): CollectionReference {
        val reviews = mock(CollectionReference::class.java)
        doReturn(reviews).`when`(doc).collection("reviews")
        return reviews
    }

    /**
     * Creates a successful review query and batched commit, with the supplied
     * review document references. Callers can override the commit result.
     */
    private fun deletionBatch(vararg reviewRefs: DocumentReference): WriteBatch {
        val reviewRows = reviewRefs.map { ref ->
            mock(DocumentSnapshot::class.java).also { row ->
                doReturn(ref).`when`(row).reference
            }
        }
        val reviewCollection = reviews()
        doReturn(completed(snapshot(*reviewRows.toTypedArray()))).`when`(reviewCollection).get()

        val batch = mock(WriteBatch::class.java)
        doReturn(batch).`when`(db).batch()
        doReturn(completed<Void?>(null)).`when`(batch).commit()
        return batch
    }

    @Test fun deleteWithReviews_noReviews_deletesCampsiteInBatch() = runBlocking<Unit> {
        val batch = deletionBatch()

        CampsiteRepository().deleteWithReviews("id")

        verify(batch).delete(doc)
        verify(batch).commit()
        verify(doc, never()).delete()
    }

    @Test fun deleteWithReviews_twoReviews_deletesAllInOneBatch() = runBlocking<Unit> {
        val firstReview = mock(DocumentReference::class.java)
        val secondReview = mock(DocumentReference::class.java)
        val batch = deletionBatch(firstReview, secondReview)

        CampsiteRepository().deleteWithReviews("id")

        val order = inOrder(batch)
        order.verify(batch).delete(firstReview)
        order.verify(batch).delete(secondReview)
        order.verify(batch).delete(doc)
        order.verify(batch).commit()
    }

    @Test fun deleteWithReviews_reviewQueryFails_neverStartsBatch() = runBlocking<Unit> {
        val failure = IllegalStateException("review read failed")
        val reviewCollection = reviews()
        doReturn(failed<QuerySnapshot>(failure)).`when`(reviewCollection).get()

        try { CampsiteRepository().deleteWithReviews("id"); fail("Expected read failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }

        verify(db, never()).batch()
    }

    @Test fun deleteWithReviews_tooManyReviews_rejectsWithoutWriting() = runBlocking<Unit> {
        val reviewRows = Array(500) { mock(DocumentSnapshot::class.java) }
        val reviewCollection = reviews()
        doReturn(completed(snapshot(*reviewRows))).`when`(reviewCollection).get()

        try {
            CampsiteRepository().deleteWithReviews("id")
            fail("Expected limit error")
        } catch (actual: IllegalStateException) {
            assertTrue(actual.message.orEmpty().contains("more than 499 reviews"))
        }

        verify(db, never()).batch()
    }

    @Test fun deleteWithReviews_commitFails_propagatesFailure() = runBlocking<Unit> {
        val batch = deletionBatch()
        val failure = IllegalStateException("commit failed")
        doReturn(failed<Void>(failure)).`when`(batch).commit()

        try { CampsiteRepository().deleteWithReviews("id"); fail("Expected commit failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }

        verify(batch).delete(doc)
        verify(batch).commit()
    }

    @Test fun listReviewsRestoresIdsAndSkipsNullObjects() = runBlocking<Unit> {
        val reviews = reviews()
        val value = Review(rating = 3)
        doReturn(completed(snapshot(row(Review::class.java, null), row(Review::class.java, value, "review")))).`when`(reviews).get()
        assertEquals(listOf(value), CampsiteRepository().listReviews("id"))
        assertEquals("review", value.id)
    }

    @Test fun addReviewSavesReviewThenUpdatesAverageAndCount() = runBlocking<Unit> {
        val reviews = reviews()
        val reviewDoc = mock(DocumentReference::class.java)
        val newReview = Review(rating = 5)
        doReturn(reviewDoc).`when`(reviews).document()
        doReturn("new-review").`when`(reviewDoc).id
        doReturn(completed<Void?>(null)).`when`(reviewDoc).set(newReview)
        doReturn(completed(snapshot(
            row(Review::class.java, Review(rating = 2), "old-review"),
            row(Review::class.java, Review(rating = 5), "new-review")
        ))).`when`(reviews).get()
        doReturn(completed<Void?>(null)).`when`(doc).update("ratingAverage", 3.5, "ratingCount", 2)
        CampsiteRepository().addReview("id", newReview)
        assertEquals("new-review", newReview.id)
        val order = inOrder(reviewDoc, reviews, doc)
        order.verify(reviewDoc).set(newReview)
        order.verify(reviews).get()
        order.verify(doc).update("ratingAverage", 3.5, "ratingCount", 2)
    }

    @Test fun failedReviewWriteDoesNotReadOrUpdateRatings() = runBlocking<Unit> {
        val reviews = reviews()
        val reviewDoc = mock(DocumentReference::class.java)
        val value = Review(rating = 4)
        val failure = IllegalStateException("review failed")
        doReturn(reviewDoc).`when`(reviews).document()
        doReturn("review").`when`(reviewDoc).id
        doReturn(failed<Void>(failure)).`when`(reviewDoc).set(value)
        try { CampsiteRepository().addReview("id", value); fail("Expected write failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
        verify(reviews, never()).get()
        verify(doc, atLeastOnce()).collection("reviews")
        verifyNoMoreInteractions(doc)
    }

    @Test fun failedReviewReadDoesNotUpdateRatings() = runBlocking<Unit> {
        val reviews = reviews()
        val reviewDoc = generatedDocument("new-review")
        doReturn(reviewDoc).`when`(reviews).document()
        val failure = IllegalStateException("review read failed")
        doReturn(failed<QuerySnapshot>(failure)).`when`(reviews).get()
        try { CampsiteRepository().addReview("id", Review(rating = 4)); fail("Expected review read failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
        verify(reviewDoc).set(anyPayload())
        verify(doc, atLeastOnce()).collection("reviews")
        verifyNoMoreInteractions(doc)
    }

    @Test fun firstReviewAggregateExcludesUnmappedRows() = runBlocking<Unit> {
        val reviews = reviews()
        val reviewDoc = generatedDocument("new-review")
        val review = Review(rating = 4)
        doReturn(reviewDoc).`when`(reviews).document()
        doReturn(completed(snapshot(
            row(Review::class.java, null, "unmapped"),
            row(Review::class.java, review, "new-review")
        ))).`when`(reviews).get()
        doReturn(completed<Void?>(null)).`when`(doc).update("ratingAverage", 4.0, "ratingCount", 1)
        CampsiteRepository().addReview("id", review)
        verify(doc).update("ratingAverage", 4.0, "ratingCount", 1)
    }

    @Test fun failedRatingUpdateReachesCaller() = runBlocking<Unit> {
        val reviews = reviews()
        val reviewDoc = generatedDocument("new-review")
        val review = Review(rating = 4)
        doReturn(reviewDoc).`when`(reviews).document()
        doReturn(completed(snapshot(row(Review::class.java, review, "new-review")))).`when`(reviews).get()
        val failure = IllegalStateException("rating update failed")
        doReturn(failed<Void>(failure)).`when`(doc).update("ratingAverage", 4.0, "ratingCount", 1)
        try { CampsiteRepository().addReview("id", review); fail("Expected aggregate failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
        verify(reviewDoc).set(review)
    }

    @Test fun listReviewsPropagatesFailure() = runBlocking<Unit> {
        val reviews = reviews()
        val failure = IllegalStateException("review list failed")
        doReturn(failed<QuerySnapshot>(failure)).`when`(reviews).get()
        try { CampsiteRepository().listReviews("id"); fail("Expected review-list failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }
}