package com.turing.campsite.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import com.turing.campsite.data.model.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*

/**
 * Tests [CampsiteRepository] against mocked Firebase boundaries.
 *
 * Covers local mapping, branching and error behavior; does not verify live persistence or security rules.
 */
class CampsiteRepositoryTest : FirestoreTestFixture("campsites") {

    /**
     * Given a mapped document with a stale model ID, expects the returned object to use the snapshot ID.
     */
    @Test fun getRestoresDocumentId() = runBlocking<Unit> {
        val value = Campsite(id = "stale")
        doReturn(completed(row(Campsite::class.java, value, "canonical"))).`when`(doc).get()
        val result = CampsiteRepository().get("id")
        assertSame(value, result)
        assertEquals("canonical", result!!.id)
    }
    /**
     * Given a snapshot that cannot map to a model, expects a null result.
     */
    @Test fun getMissingDocumentReturnsNull() = runBlocking<Unit> {
        doReturn(completed(row(Campsite::class.java, null))).`when`(doc).get()
        assertNull(CampsiteRepository().get("id"))
    }
    /**
     * Given a failed document read, expects the same exception to reach the caller.
     */
    @Test fun getPropagatesFailure() = runBlocking<Unit> {
        val failure = IllegalStateException("read failed")
        doReturn(failed<DocumentSnapshot>(failure)).`when`(doc).get()
        try { CampsiteRepository().get("id"); fail("Expected read failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /**
     * Given a successful write, expects the generated document ID on the input model and as the return value.
     */
    @Test fun addAssignsAndReturnsGeneratedId() = runBlocking<Unit> {
        val value = Campsite(id = "previous")
        assertEquals("generated", CampsiteRepository().add(value))
        assertEquals("generated", value.id)
        verify(generatedDoc).set(value)
        verify(collection).document()
        verify(collection, never()).document(anyString())
        verify(doc, never()).set(anyPayload())
    }
    /**
     * Given a failed write, expects the same exception without promising when the input ID changes.
     */
    @Test fun addPropagatesWriteFailure() = runBlocking<Unit> {
        val value = Campsite()
        val failure = IllegalStateException("write failed")
        doReturn(failed<Void>(failure)).`when`(generatedDoc).set(value)
        try { CampsiteRepository().add(value); fail("Expected write failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /**
     * Given mapped and unmappable results from the all-records query, expects only mapped records with snapshot IDs.
     */
    @Test fun listAllMapsIdsAndSkipsNullObjects() = runBlocking<Unit> {
        val value = Campsite(id = "stale")
        doReturn(completed(snapshot(row(Campsite::class.java, null), row(Campsite::class.java, value, "canonical")))).`when`(collection).get()
        val result = CampsiteRepository().listAll()
        assertEquals(listOf(value), result)
        assertEquals("canonical", result.single().id)
    }
    /**
     * Given no results from the all-records query, expects an empty list.
     */
    @Test fun listAllReturnsEmptyList() = runBlocking<Unit> {
        doReturn(completed(snapshot())).`when`(collection).get()
        assertTrue(CampsiteRepository().listAll().isEmpty())
    }
    /**
     * Given failure of the all-records query, expects the same exception to propagate.
     */
    @Test fun listAllPropagatesQueryFailure() = runBlocking<Unit> {
        val failure = IllegalStateException("query failed")
        doReturn(failed<QuerySnapshot>(failure)).`when`(collection).get()
        try { CampsiteRepository().listAll(); fail("Expected query failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /**
     * Given mapped and unmappable results from the owner-filtered query, expects only mapped records with snapshot IDs.
     */
    @Test fun listByOwnerMapsIdsAndSkipsNullObjects() = runBlocking<Unit> {
        val query = mock(Query::class.java)
        doReturn(query).`when`(collection).whereEqualTo("ownerId", "owner")
        val value = Campsite(id = "stale")
        doReturn(completed(snapshot(row(Campsite::class.java, null), row(Campsite::class.java, value, "canonical")))).`when`(query).get()
        val result = CampsiteRepository().listByOwner("owner")
        assertEquals(listOf(value), result)
        assertEquals("canonical", result.single().id)
    }
    /**
     * Given no results from the owner-filtered query, expects an empty list.
     */
    @Test fun listByOwnerReturnsEmptyList() = runBlocking<Unit> {
        val query = mock(Query::class.java)
        doReturn(query).`when`(collection).whereEqualTo("ownerId", "owner")
        doReturn(completed(snapshot())).`when`(query).get()
        assertTrue(CampsiteRepository().listByOwner("owner").isEmpty())
    }
    /**
     * Given failure of the owner-filtered query, expects the same exception to propagate.
     */
    @Test fun listByOwnerPropagatesQueryFailure() = runBlocking<Unit> {
        val query = mock(Query::class.java)
        doReturn(query).`when`(collection).whereEqualTo("ownerId", "owner")
        val failure = IllegalStateException("query failed")
        doReturn(failed<QuerySnapshot>(failure)).`when`(query).get()
        try { CampsiteRepository().listByOwner("owner"); fail("Expected query failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /**
     * Given an existing model ID, expects a full document set using that model.
     */
    @Test fun updateReplacesDocument() = runBlocking<Unit> {
        val value = Campsite(id = "id")
        CampsiteRepository().update(value)
        verify(doc).set(value)
    }
    /**
     * Given a failed replacement write, expects the same exception to propagate.
     */
    @Test fun updatePropagatesFailure() = runBlocking<Unit> {
        val value = Campsite(id = "id")
        val failure = IllegalStateException("write failed")
        doReturn(failed<Void>(failure)).`when`(doc).set(value)
        try { CampsiteRepository().update(value); fail("Expected write failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /**
     * Given a document ID, expects deletion of that document.
     */
    @Test fun deleteDeletesRequestedDocument() = runBlocking<Unit> {
        CampsiteRepository().delete("id")
        verify(doc).delete()
    }
    /**
     * Given a failed deletion, expects the same exception to propagate.
     */
    @Test fun deletePropagatesFailure() = runBlocking<Unit> {
        val failure = IllegalStateException("delete failed")
        doReturn(failed<Void>(failure)).`when`(doc).delete()
        try { CampsiteRepository().delete("id"); fail("Expected delete failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /** Stubs the current campsite document’s reviews subcollection. */
    private fun reviews(): CollectionReference {
        val reviews = mock(CollectionReference::class.java)
        doReturn(reviews).`when`(doc).collection("reviews")
        return reviews
    }
    /**
     * Given mapped and unmappable review snapshots, expects only mapped reviews with snapshot IDs.
     */
    @Test fun listReviewsRestoresIdsAndSkipsNullObjects() = runBlocking<Unit> {
        val reviews = reviews()
        val value = Review(rating = 3)
        doReturn(completed(snapshot(row(Review::class.java, null), row(Review::class.java, value, "review")))).`when`(reviews).get()
        assertEquals(listOf(value), CampsiteRepository().listReviews("id"))
        assertEquals("review", value.id)
    }
    /**
     * Given ratings 2 and 5, expects review creation before aggregation, then an average of 3.5 and count of 2.
     */
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
    /**
     * Given a failed review write, expects the same exception and verifies the rating-source query is not reached.
     */
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
    /** Does not overwrite the rating aggregate when the saved reviews cannot be read. */
    @Test fun failedReviewReadDoesNotUpdateRatings() = runBlocking<Unit> {
        val reviews = reviews()
        val reviewDoc = generatedDocument("new-review")
        doReturn(reviewDoc).`when`(reviews).document()
        val failure = IllegalStateException("review read failed")
        doReturn(failed<QuerySnapshot>(failure)).`when`(reviews).get()
        try { CampsiteRepository().addReview("id", Review(rating = 4)); fail("Expected read failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
        verify(reviewDoc).set(anyPayload())
        verify(doc, atLeastOnce()).collection("reviews")
        verifyNoMoreInteractions(doc)
    }

    /** Computes the first valid review as average 4.0 and count 1, excluding an unmappable row. */
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

    /** Propagates a failed aggregate update even though the review write itself succeeded. */
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

    /** Propagates a failed review listing without fabricating an empty list. */
    @Test fun listReviewsPropagatesFailure() = runBlocking<Unit> {
        val reviews = reviews()
        val failure = IllegalStateException("review list failed")
        doReturn(failed<QuerySnapshot>(failure)).`when`(reviews).get()
        try { CampsiteRepository().listReviews("id"); fail("Expected review-list failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

}
