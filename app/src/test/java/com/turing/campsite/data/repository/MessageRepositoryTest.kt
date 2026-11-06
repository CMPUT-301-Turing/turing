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
 * Tests [MessageRepository] against mocked Firebase boundaries.
 *
 * Covers local mapping, branching and error behavior; does not verify live persistence or security rules.
 */
class MessageRepositoryTest : FirestoreTestFixture("messages") {

    /**
     * Given a successful write, expects the generated document ID on the input model and as the return value.
     */
    @Test fun addAssignsAndReturnsGeneratedId() = runBlocking<Unit> {
        val value = Message(id = "previous")
        assertEquals("generated", MessageRepository().add(value))
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
        val value = Message()
        val failure = IllegalStateException("write failed")
        doReturn(failed<Void>(failure)).`when`(generatedDoc).set(value)
        try { MessageRepository().add(value); fail("Expected write failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /**
     * Given mapped and unmappable results from the reservation-filtered query, expects only mapped records with snapshot IDs.
     */
    @Test fun listForReservationMapsIdsAndSkipsNullObjects() = runBlocking<Unit> {
        val query = mock(Query::class.java)
        doReturn(query).`when`(collection).whereEqualTo("reservationId", "reservation")
        val value = Message(id = "stale")
        doReturn(completed(snapshot(row(Message::class.java, null), row(Message::class.java, value, "canonical")))).`when`(query).get()
        val result = MessageRepository().listForReservation("reservation")
        assertEquals(listOf(value), result)
        assertEquals("canonical", result.single().id)
    }
    /**
     * Given no results from the reservation-filtered query, expects an empty list.
     */
    @Test fun listForReservationReturnsEmptyList() = runBlocking<Unit> {
        val query = mock(Query::class.java)
        doReturn(query).`when`(collection).whereEqualTo("reservationId", "reservation")
        doReturn(completed(snapshot())).`when`(query).get()
        assertTrue(MessageRepository().listForReservation("reservation").isEmpty())
    }
    /**
     * Given failure of the reservation-filtered query, expects the same exception to propagate.
     */
    @Test fun listForReservationPropagatesQueryFailure() = runBlocking<Unit> {
        val query = mock(Query::class.java)
        doReturn(query).`when`(collection).whereEqualTo("reservationId", "reservation")
        val failure = IllegalStateException("query failed")
        doReturn(failed<QuerySnapshot>(failure)).`when`(query).get()
        try { MessageRepository().listForReservation("reservation"); fail("Expected query failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /**
     * Given out-of-order timestamps, expects oldest-first conversation order.
     */
    @Test fun listForReservationOrdersTimestamps() = runBlocking<Unit> {
        val query = mock(Query::class.java)
        doReturn(query).`when`(collection).whereEqualTo("reservationId", "reservation")
        val earliest = Message(createdAt = Timestamp(1, 999_999_999))
        val early = Message(createdAt = Timestamp(2, 100))
        val late = Message(createdAt = Timestamp(2, 900))
        val latest = Message(createdAt = Timestamp(3, 0))
        doReturn(completed(snapshot(
            row(Message::class.java, early, "early"),
            row(Message::class.java, latest, "latest"),
            row(Message::class.java, earliest, "earliest"),
            row(Message::class.java, late, "late")
        ))).`when`(query).get()
        assertEquals(listOf("earliest", "early", "late", "latest"), MessageRepository().listForReservation("reservation").map { it.id })
    }

    /**
     * Given mapped and unmappable results from the all-records query, expects only mapped records with snapshot IDs.
     */
    @Test fun listAllMapsIdsAndSkipsNullObjects() = runBlocking<Unit> {
        val value = Message(id = "stale")
        doReturn(completed(snapshot(row(Message::class.java, null), row(Message::class.java, value, "canonical")))).`when`(collection).get()
        val result = MessageRepository().listAll()
        assertEquals(listOf(value), result)
        assertEquals("canonical", result.single().id)
    }
    /**
     * Given no results from the all-records query, expects an empty list.
     */
    @Test fun listAllReturnsEmptyList() = runBlocking<Unit> {
        doReturn(completed(snapshot())).`when`(collection).get()
        assertTrue(MessageRepository().listAll().isEmpty())
    }
    /**
     * Given failure of the all-records query, expects the same exception to propagate.
     */
    @Test fun listAllPropagatesQueryFailure() = runBlocking<Unit> {
        val failure = IllegalStateException("query failed")
        doReturn(failed<QuerySnapshot>(failure)).`when`(collection).get()
        try { MessageRepository().listAll(); fail("Expected query failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /**
     * Given shuffled timestamps with same-second nanoseconds, expects newest-first result order.
     */
    @Test fun listAllOrdersTimestamps() = runBlocking<Unit> {
        val earliest = Message(createdAt = Timestamp(1, 999_999_999))
        val early = Message(createdAt = Timestamp(2, 100))
        val late = Message(createdAt = Timestamp(2, 900))
        val latest = Message(createdAt = Timestamp(3, 0))
        doReturn(completed(snapshot(
            row(Message::class.java, early, "early"),
            row(Message::class.java, latest, "latest"),
            row(Message::class.java, earliest, "earliest"),
            row(Message::class.java, late, "late")
        ))).`when`(collection).get()
        assertEquals(listOf("latest", "late", "early", "earliest"), MessageRepository().listAll().map { it.id })
    }
}
