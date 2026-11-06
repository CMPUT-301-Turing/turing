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
 * Tests [UserRepository] against mocked Firebase boundaries.
 *
 * Covers local mapping, branching and error behavior; does not verify live persistence or security rules.
 */
class UserRepositoryTest : FirestoreTestFixture("users") {

    /**
     * Given a mapped document with a stale model ID, expects the returned object to use the snapshot ID.
     */
    @Test fun getRestoresDocumentId() = runBlocking<Unit> {
        val value = User(id = "stale")
        doReturn(completed(row(User::class.java, value, "canonical"))).`when`(doc).get()
        val result = UserRepository().get("id")
        assertSame(value, result)
        assertEquals("canonical", result!!.id)
    }
    /**
     * Given a snapshot that cannot map to a model, expects a null result.
     */
    @Test fun getMissingDocumentReturnsNull() = runBlocking<Unit> {
        doReturn(completed(row(User::class.java, null))).`when`(doc).get()
        assertNull(UserRepository().get("id"))
    }
    /**
     * Given a failed document read, expects the same exception to reach the caller.
     */
    @Test fun getPropagatesFailure() = runBlocking<Unit> {
        val failure = IllegalStateException("read failed")
        doReturn(failed<DocumentSnapshot>(failure)).`when`(doc).get()
        try { UserRepository().get("id"); fail("Expected read failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /**
     * Given mapped and unmappable results from the all-records query, expects only mapped records with snapshot IDs.
     */
    @Test fun listAllMapsIdsAndSkipsNullObjects() = runBlocking<Unit> {
        val value = User(id = "stale")
        doReturn(completed(snapshot(row(User::class.java, null), row(User::class.java, value, "canonical")))).`when`(collection).get()
        val result = UserRepository().listAll()
        assertEquals(listOf(value), result)
        assertEquals("canonical", result.single().id)
    }
    /**
     * Given no results from the all-records query, expects an empty list.
     */
    @Test fun listAllReturnsEmptyList() = runBlocking<Unit> {
        doReturn(completed(snapshot())).`when`(collection).get()
        assertTrue(UserRepository().listAll().isEmpty())
    }
    /**
     * Given failure of the all-records query, expects the same exception to propagate.
     */
    @Test fun listAllPropagatesQueryFailure() = runBlocking<Unit> {
        val failure = IllegalStateException("query failed")
        doReturn(failed<QuerySnapshot>(failure)).`when`(collection).get()
        try { UserRepository().listAll(); fail("Expected query failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /**
     * Given an existing user ID, expects a full document set using that user.
     */
    @Test fun saveReplacesDocument() = runBlocking<Unit> {
        val value = User(id = "id")
        UserRepository().save(value)
        verify(doc).set(value)
    }
    /**
     * Given a failed save, expects the same exception to propagate.
     */
    @Test fun savePropagatesFailure() = runBlocking<Unit> {
        val value = User(id = "id")
        val failure = IllegalStateException("write failed")
        doReturn(failed<Void>(failure)).`when`(doc).set(value)
        try { UserRepository().save(value); fail("Expected write failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /**
     * Given a document ID, expects deletion of that document.
     */
    @Test fun deleteDeletesRequestedDocument() = runBlocking<Unit> {
        UserRepository().delete("id")
        verify(doc).delete()
    }
    /**
     * Given a failed deletion, expects the same exception to propagate.
     */
    @Test fun deletePropagatesFailure() = runBlocking<Unit> {
        val failure = IllegalStateException("delete failed")
        doReturn(failed<Void>(failure)).`when`(doc).delete()
        try { UserRepository().delete("id"); fail("Expected delete failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /** Keeps distinct nonempty profile values in their correct fields without replacing the document. */
    @Test fun updateProfileWritesOnlyProfileFields() = runBlocking<Unit> {
        doReturn(completed<Void?>(null)).`when`(doc).update(
            "name", "Mira Chen", "email", "mira@example.invalid", "phone", "+1-780-555-0108")
        UserRepository().updateProfile("id", "Mira Chen", "mira@example.invalid", "+1-780-555-0108")
        verify(doc).update("name", "Mira Chen", "email", "mira@example.invalid", "phone", "+1-780-555-0108")
        verifyNoMoreInteractions(doc)
        verifyNoInteractions(generatedDoc)
    }
    /**
     * For enabled and disabled preferences, expects the corresponding Boolean update.
     */
    @Test fun setNotificationsSupportsBothValues() = runBlocking<Unit> {
        for (enabled in listOf(true, false)) {
            doReturn(completed<Void?>(null)).`when`(doc).update("notificationsEnabled", enabled)
            UserRepository().setNotificationsEnabled("id", enabled)
            verify(doc).update("notificationsEnabled", enabled)
        }
    }
    /** Allows clearing optional contact fields without retaining the previous strings. */
    @Test fun updateProfileCanClearContactFields() = runBlocking<Unit> {
        doReturn(completed<Void?>(null)).`when`(doc).update("name", "Mira Chen", "email", "", "phone", "")
        UserRepository().updateProfile("id", "Mira Chen", "", "")
        verify(doc).update("name", "Mira Chen", "email", "", "phone", "")
        verifyNoMoreInteractions(doc)
    }

    /** Propagates profile write failure rather than reporting a successful edit. */
    @Test fun updateProfilePropagatesFailure() = runBlocking<Unit> {
        val failure = IllegalStateException("profile write failed")
        doReturn(failed<Void>(failure)).`when`(doc).update(
            "name", "Mira Chen", "email", "mira@example.invalid", "phone", "+1-780-555-0108")
        try {
            UserRepository().updateProfile("id", "Mira Chen", "mira@example.invalid", "+1-780-555-0108")
            fail("Expected profile failure")
        } catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /** Propagates failure to persist a notification opt-out. */
    @Test fun setNotificationsPropagatesFailure() = runBlocking<Unit> {
        val failure = IllegalStateException("preference write failed")
        doReturn(failed<Void>(failure)).`when`(doc).update("notificationsEnabled", false)
        try { UserRepository().setNotificationsEnabled("id", false); fail("Expected preference failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

}
