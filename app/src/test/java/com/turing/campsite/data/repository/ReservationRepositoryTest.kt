/* Verifies reservation queries and rejection transaction behavior with mocked Firestore. */
package com.turing.campsite.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import com.turing.campsite.data.model.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import java.util.Date

/**
 * Tests [ReservationRepository] against mocked Firebase boundaries.
 *
 * Covers local mapping, branching and error behavior; does not verify live persistence or security rules.
 */
class ReservationRepositoryTest : FirestoreTestFixture("reservations") {

    /**
     * Given a mapped document with a stale model ID, expects the returned object to use the snapshot ID.
     */
    @Test fun getRestoresDocumentId() = runBlocking<Unit> {
        val value = Reservation(id = "stale")
        doReturn(completed(row(Reservation::class.java, value, "canonical"))).`when`(doc).get()
        val result = ReservationRepository().get("id")
        assertSame(value, result)
        assertEquals("canonical", result!!.id)
    }
    /**
     * Given a snapshot that cannot map to a model, expects a null result.
     */
    @Test fun getMissingDocumentReturnsNull() = runBlocking<Unit> {
        doReturn(completed(row(Reservation::class.java, null))).`when`(doc).get()
        assertNull(ReservationRepository().get("id"))
    }
    /**
     * Given a failed document read, expects the same exception to reach the caller.
     */
    @Test fun getPropagatesFailure() = runBlocking<Unit> {
        val failure = IllegalStateException("read failed")
        doReturn(failed<DocumentSnapshot>(failure)).`when`(doc).get()
        try { ReservationRepository().get("id"); fail("Expected read failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /**
     * Given a successful write, expects the generated document ID on the input model and as the return value.
     */
    @Test fun addAssignsAndReturnsGeneratedId() = runBlocking<Unit> {
        val value = Reservation(id = "previous")
        assertEquals("generated", ReservationRepository().add(value))
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
        val value = Reservation()
        val failure = IllegalStateException("write failed")
        doReturn(failed<Void>(failure)).`when`(generatedDoc).set(value)
        try { ReservationRepository().add(value); fail("Expected write failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /**
     * Given mapped and unmappable results from the entrant-filtered query, expects only mapped records with snapshot IDs.
     */
    @Test fun listByEntrantMapsIdsAndSkipsNullObjects() = runBlocking<Unit> {
        val query = mock(Query::class.java)
        doReturn(query).`when`(collection).whereEqualTo("entrantId", "entrant")
        val value = Reservation(id = "stale")
        doReturn(completed(snapshot(row(Reservation::class.java, null), row(Reservation::class.java, value, "canonical")))).`when`(query).get()
        val result = ReservationRepository().listByEntrant("entrant")
        assertEquals(listOf(value), result)
        assertEquals("canonical", result.single().id)
    }
    /**
     * Given no results from the entrant-filtered query, expects an empty list.
     */
    @Test fun listByEntrantReturnsEmptyList() = runBlocking<Unit> {
        val query = mock(Query::class.java)
        doReturn(query).`when`(collection).whereEqualTo("entrantId", "entrant")
        doReturn(completed(snapshot())).`when`(query).get()
        assertTrue(ReservationRepository().listByEntrant("entrant").isEmpty())
    }
    /**
     * Given failure of the entrant-filtered query, expects the same exception to propagate.
     */
    @Test fun listByEntrantPropagatesQueryFailure() = runBlocking<Unit> {
        val query = mock(Query::class.java)
        doReturn(query).`when`(collection).whereEqualTo("entrantId", "entrant")
        val failure = IllegalStateException("query failed")
        doReturn(failed<QuerySnapshot>(failure)).`when`(query).get()
        try { ReservationRepository().listByEntrant("entrant"); fail("Expected query failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /**
     * Given mapped and unmappable results from the owner-filtered query, expects only mapped records with snapshot IDs.
     */
    @Test fun listByOwnerMapsIdsAndSkipsNullObjects() = runBlocking<Unit> {
        val query = mock(Query::class.java)
        doReturn(query).`when`(collection).whereEqualTo("ownerId", "owner")
        val value = Reservation(id = "stale")
        doReturn(completed(snapshot(row(Reservation::class.java, null), row(Reservation::class.java, value, "canonical")))).`when`(query).get()
        val result = ReservationRepository().listByOwner("owner")
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
        assertTrue(ReservationRepository().listByOwner("owner").isEmpty())
    }
    /**
     * Given failure of the owner-filtered query, expects the same exception to propagate.
     */
    @Test fun listByOwnerPropagatesQueryFailure() = runBlocking<Unit> {
        val query = mock(Query::class.java)
        doReturn(query).`when`(collection).whereEqualTo("ownerId", "owner")
        val failure = IllegalStateException("query failed")
        doReturn(failed<QuerySnapshot>(failure)).`when`(query).get()
        try { ReservationRepository().listByOwner("owner"); fail("Expected query failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /** An eligible booking changes only the status field in the transaction. */
    @Test fun rejectForOwnerWritesOnlyRejectedStatus() = runBlocking<Unit> {
        val transaction = stubRejectionTransaction(Reservation(ownerId = "owner"))

        ReservationRepository().rejectForOwner("id", "owner")

        verify(transaction).update(doc, "status", ReservationStatus.REJECTED.name)
        verify(doc, never()).update(anyString(), anyPayload())
    }

    /** A cancellation received after the screen was loaded must not be overwritten. */
    @Test fun rejectForOwnerDoesNotOverwriteAnAlreadyCancelledBooking() = runBlocking<Unit> {
        val transaction = stubRejectionTransaction(
            Reservation(ownerId = "owner", status = ReservationStatus.CANCELLED)
        )

        assertRejectionUnavailable()
        verify(transaction, never()).update(doc, "status", ReservationStatus.REJECTED.name)
    }

    /** A past booking may still have a stored ACTIVE status on this release. */
    @Test fun rejectForOwnerDoesNotRejectAnElapsedBooking() = runBlocking<Unit> {
        val transaction = stubRejectionTransaction(
            Reservation(ownerId = "owner", endDate = Timestamp(Date(0)))
        )

        assertRejectionUnavailable()
        verify(transaction, never()).update(doc, "status", ReservationStatus.REJECTED.name)
    }

    private fun stubRejectionTransaction(reservation: Reservation): Transaction {
        val transaction = mock(Transaction::class.java)
        doReturn(row(Reservation::class.java, reservation)).`when`(transaction).get(doc)
        doAnswer { invocation ->
            val update = invocation.getArgument<Transaction.Function<Unit>>(0)
            try {
                completed(update.apply(transaction))
            } catch (error: Exception) {
                failed<Unit>(error)
            }
        }.`when`(db).runTransaction(any<Transaction.Function<Unit>>() ?: Transaction.Function { Unit })
        return transaction
    }

    private suspend fun assertRejectionUnavailable() {
        try {
            ReservationRepository().rejectForOwner("id", "owner")
            fail("Expected stale booking to be rejected without a write")
        } catch (_: IllegalStateException) {
            // The status check halted the transaction.
        }
    }

    /**
     * For each status enum, expects an update of the status field using its name.
     */
    @Test fun setStatusWritesEveryEnumByName() = runBlocking<Unit> {
        for (status in ReservationStatus.values()) {
            doReturn(completed<Void?>(null)).`when`(doc).update("status", status.name)
            ReservationRepository().setStatus("id", status)
            verify(doc).update("status", status.name)
        }
    }
    /**
     * Given a failed status update, expects the same exception to propagate.
     */
    @Test fun setStatusPropagatesFailure() = runBlocking<Unit> {
        val status = ReservationStatus.values().first()
        val failure = IllegalStateException("update failed")
        doReturn(failed<Void>(failure)).`when`(doc).update("status", status.name)
        try { ReservationRepository().setStatus("id", status); fail("Expected update failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /**
     * Given an active-campsite query, expects both campsite-ID and active-status filters before returning mapped reservations.
     */
    @Test fun listActiveForCampsiteCombinesBothFilters() = runBlocking<Unit> {
        val byCampsite = mock(Query::class.java)
        val active = mock(Query::class.java)
        doReturn(byCampsite).`when`(collection).whereEqualTo("campsiteId", "camp")
        doReturn(active).`when`(byCampsite).whereEqualTo("status", "ACTIVE")
        val value = Reservation()
        doReturn(completed(snapshot(row(Reservation::class.java, null), row(Reservation::class.java, value, "canonical")))).`when`(active).get()
        assertEquals(listOf(value), ReservationRepository().listActiveForCampsite("camp"))
        assertEquals("canonical", value.id)
    }
}
