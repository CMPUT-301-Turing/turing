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
 * Tests [NotificationRepository] against mocked Firebase boundaries.
 *
 * Covers local mapping, branching and error behavior; does not verify live persistence or security rules.
 */
class NotificationRepositoryTest : FirestoreTestFixture("notifications") {

    /**
     * Given a successful write, expects the generated document ID on the input model and as the return value.
     */
    @Test fun addAssignsAndReturnsGeneratedId() = runBlocking<Unit> {
        val value = Notification(id = "previous")
        assertEquals("generated", NotificationRepository().add(value))
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
        val value = Notification()
        val failure = IllegalStateException("write failed")
        doReturn(failed<Void>(failure)).`when`(generatedDoc).set(value)
        try { NotificationRepository().add(value); fail("Expected write failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /**
     * Given mapped and unmappable results from the recipient-filtered query, expects only mapped records with snapshot IDs.
     */
    @Test fun listForUserMapsIdsAndSkipsNullObjects() = runBlocking<Unit> {
        val query = mock(Query::class.java)
        doReturn(query).`when`(collection).whereEqualTo("toUserId", "user")
        val value = Notification(id = "stale")
        doReturn(completed(snapshot(row(Notification::class.java, null), row(Notification::class.java, value, "canonical")))).`when`(query).get()
        val result = NotificationRepository().listForUser("user")
        assertEquals(listOf(value), result)
        assertEquals("canonical", result.single().id)
    }
    /**
     * Given no results from the recipient-filtered query, expects an empty list.
     */
    @Test fun listForUserReturnsEmptyList() = runBlocking<Unit> {
        val query = mock(Query::class.java)
        doReturn(query).`when`(collection).whereEqualTo("toUserId", "user")
        doReturn(completed(snapshot())).`when`(query).get()
        assertTrue(NotificationRepository().listForUser("user").isEmpty())
    }
    /**
     * Given failure of the recipient-filtered query, expects the same exception to propagate.
     */
    @Test fun listForUserPropagatesQueryFailure() = runBlocking<Unit> {
        val query = mock(Query::class.java)
        doReturn(query).`when`(collection).whereEqualTo("toUserId", "user")
        val failure = IllegalStateException("query failed")
        doReturn(failed<QuerySnapshot>(failure)).`when`(query).get()
        try { NotificationRepository().listForUser("user"); fail("Expected query failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /**
     * Given shuffled timestamps with same-second nanoseconds, expects newest-first result order.
     */
    @Test fun listForUserOrdersTimestamps() = runBlocking<Unit> {
        val query = mock(Query::class.java)
        doReturn(query).`when`(collection).whereEqualTo("toUserId", "user")
        val earliest = Notification(createdAt = Timestamp(1, 999_999_999))
        val early = Notification(createdAt = Timestamp(2, 100))
        val late = Notification(createdAt = Timestamp(2, 900))
        val latest = Notification(createdAt = Timestamp(3, 0))
        doReturn(completed(snapshot(
            row(Notification::class.java, early, "early"),
            row(Notification::class.java, latest, "latest"),
            row(Notification::class.java, earliest, "earliest"),
            row(Notification::class.java, late, "late")
        ))).`when`(query).get()
        assertEquals(listOf("latest", "late", "early", "earliest"), NotificationRepository().listForUser("user").map { it.id })
    }

    /**
     * Given mapped and unmappable results from the sender-filtered query, expects only mapped records with snapshot IDs.
     */
    @Test fun listSentByMapsIdsAndSkipsNullObjects() = runBlocking<Unit> {
        val query = mock(Query::class.java)
        doReturn(query).`when`(collection).whereEqualTo("fromUserId", "user")
        val value = Notification(id = "stale")
        doReturn(completed(snapshot(row(Notification::class.java, null), row(Notification::class.java, value, "canonical")))).`when`(query).get()
        val result = NotificationRepository().listSentBy("user")
        assertEquals(listOf(value), result)
        assertEquals("canonical", result.single().id)
    }
    /**
     * Given no results from the sender-filtered query, expects an empty list.
     */
    @Test fun listSentByReturnsEmptyList() = runBlocking<Unit> {
        val query = mock(Query::class.java)
        doReturn(query).`when`(collection).whereEqualTo("fromUserId", "user")
        doReturn(completed(snapshot())).`when`(query).get()
        assertTrue(NotificationRepository().listSentBy("user").isEmpty())
    }
    /**
     * Given failure of the sender-filtered query, expects the same exception to propagate.
     */
    @Test fun listSentByPropagatesQueryFailure() = runBlocking<Unit> {
        val query = mock(Query::class.java)
        doReturn(query).`when`(collection).whereEqualTo("fromUserId", "user")
        val failure = IllegalStateException("query failed")
        doReturn(failed<QuerySnapshot>(failure)).`when`(query).get()
        try { NotificationRepository().listSentBy("user"); fail("Expected query failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /**
     * Given shuffled timestamps with same-second nanoseconds, expects newest-first result order.
     */
    @Test fun listSentByOrdersTimestamps() = runBlocking<Unit> {
        val query = mock(Query::class.java)
        doReturn(query).`when`(collection).whereEqualTo("fromUserId", "user")
        val earliest = Notification(createdAt = Timestamp(1, 999_999_999))
        val early = Notification(createdAt = Timestamp(2, 100))
        val late = Notification(createdAt = Timestamp(2, 900))
        val latest = Notification(createdAt = Timestamp(3, 0))
        doReturn(completed(snapshot(
            row(Notification::class.java, early, "early"),
            row(Notification::class.java, latest, "latest"),
            row(Notification::class.java, earliest, "earliest"),
            row(Notification::class.java, late, "late")
        ))).`when`(query).get()
        assertEquals(listOf("latest", "late", "early", "earliest"), NotificationRepository().listSentBy("user").map { it.id })
    }

    /**
     * Given mapped and unmappable results from the all-records query, expects only mapped records with snapshot IDs.
     */
    @Test fun listAllMapsIdsAndSkipsNullObjects() = runBlocking<Unit> {
        val value = Notification(id = "stale")
        doReturn(completed(snapshot(row(Notification::class.java, null), row(Notification::class.java, value, "canonical")))).`when`(collection).get()
        val result = NotificationRepository().listAll()
        assertEquals(listOf(value), result)
        assertEquals("canonical", result.single().id)
    }
    /**
     * Given no results from the all-records query, expects an empty list.
     */
    @Test fun listAllReturnsEmptyList() = runBlocking<Unit> {
        doReturn(completed(snapshot())).`when`(collection).get()
        assertTrue(NotificationRepository().listAll().isEmpty())
    }
    /**
     * Given failure of the all-records query, expects the same exception to propagate.
     */
    @Test fun listAllPropagatesQueryFailure() = runBlocking<Unit> {
        val failure = IllegalStateException("query failed")
        doReturn(failed<QuerySnapshot>(failure)).`when`(collection).get()
        try { NotificationRepository().listAll(); fail("Expected query failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /**
     * Given shuffled timestamps with same-second nanoseconds, expects newest-first result order.
     */
    @Test fun listAllOrdersTimestamps() = runBlocking<Unit> {
        val earliest = Notification(createdAt = Timestamp(1, 999_999_999))
        val early = Notification(createdAt = Timestamp(2, 100))
        val late = Notification(createdAt = Timestamp(2, 900))
        val latest = Notification(createdAt = Timestamp(3, 0))
        doReturn(completed(snapshot(
            row(Notification::class.java, early, "early"),
            row(Notification::class.java, latest, "latest"),
            row(Notification::class.java, earliest, "earliest"),
            row(Notification::class.java, late, "late")
        ))).`when`(collection).get()
        assertEquals(listOf("latest", "late", "early", "earliest"), NotificationRepository().listAll().map { it.id })
    }

    /**
     * Given a notification ID, expects its read field to be updated to true.
     */
    @Test fun markReadWritesTrue() = runBlocking<Unit> {
        doReturn(completed<Void?>(null)).`when`(doc).update("read", true)
        NotificationRepository().markRead("id")
        verify(doc).update("read", true)
    }
    /**
     * Given no recipients, expects no generated document to be requested.
     */
    @Test fun sendToManyEmptyListDoesNotWrite() = runBlocking<Unit> {
        NotificationRepository().sendToMany(emptyList(), Notification())
        verify(collection, never()).document()
    }
    /** Creates one distinct document per recipient with complete copied payloads and leaves the template unchanged. */
    @Test fun sendToManyCopiesEachRecipientWithoutMutatingTemplate() = runBlocking<Unit> {
        val template = Notification(id = "original", fromUserId = "owner",
            toUserId = "unchanged", title = "Reservation update", body = "Please confirm your stay.",
            read = false, createdAt = Timestamp(2_000, 123))
        val original = template.copy()
        val recipients = listOf("entrant-a", "entrant-b", "entrant-c")
        val references = recipients.indices.map { generatedDocument("notice-$it") }
        doReturn(references[0], references[1], references[2]).`when`(collection).document()
        val payloads = mutableListOf<Notification>()
        for (reference in references) {
            doAnswer { call ->
                val payload = call.getArgument<Notification>(0)
                assertNotSame(template, payload)
                payloads += payload.copy()
                completed<Void?>(null)
            }.`when`(reference).set(anyPayload())
        }
        NotificationRepository().sendToMany(recipients, template)
        assertEquals(recipients.mapIndexed { index, recipient ->
            original.copy(id = "notice-$index", toUserId = recipient)
        }, payloads)
        verify(collection, times(3)).document()
        references.forEach { verify(it, times(1)).set(anyPayload()) }
        verifyNoInteractions(doc)
        assertEquals(original, template)
    }
    /**
     * Given failure on the first recipient write, expects the same exception and exactly one attempted write.
     */
    @Test fun sendToManyStopsOnFirstFailure() = runBlocking<Unit> {
        val failure = IllegalStateException("send failed")
        doReturn(failed<Void>(failure)).`when`(generatedDoc).set(anyPayload())
        try { NotificationRepository().sendToMany(listOf("a", "b"), Notification()); fail("Expected write failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
        verify(generatedDoc, times(1)).set(anyPayload())
    }
    /** Propagates the failure to mark a notification read. */
    @Test fun markReadPropagatesFailure() = runBlocking<Unit> {
        val failure = IllegalStateException("mark-read failed")
        doReturn(failed<Void>(failure)).`when`(doc).update("read", true)
        try { NotificationRepository().markRead("id"); fail("Expected mark-read failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /** Reports a partial fan-out failure without writing the remaining recipient or changing the template. */
    @Test fun sendToManyStopsAfterSecondWriteFails() = runBlocking<Unit> {
        val first = generatedDocument("first")
        val second = generatedDocument("second")
        val third = generatedDocument("third")
        doReturn(first, second, third).`when`(collection).document()
        val failure = IllegalStateException("second send failed")
        doReturn(failed<Void>(failure)).`when`(second).set(anyPayload())
        val template = Notification(title = "Reminder", toUserId = "unchanged")
        val original = template.copy()
        try {
            NotificationRepository().sendToMany(listOf("a", "b", "c"), template)
            fail("Expected fan-out failure")
        } catch (actual: IllegalStateException) { assertSame(failure, actual) }
        verify(first).set(original.copy(id = "first", toUserId = "a"))
        verify(second).set(original.copy(id = "second", toUserId = "b"))
        verify(collection, times(2)).document()
        verifyNoInteractions(third)
        assertEquals(original, template)
    }

}
