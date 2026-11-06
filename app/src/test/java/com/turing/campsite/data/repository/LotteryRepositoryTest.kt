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
 * Tests [LotteryRepository] against mocked Firebase boundaries.
 *
 * Covers local mapping, branching and error behavior; does not verify live persistence or security rules.
 */
class LotteryRepositoryTest : FirestoreTestFixture("lotteries") {

    /**
     * Given a mapped document with a stale model ID, expects the returned object to use the snapshot ID.
     */
    @Test fun getRestoresDocumentId() = runBlocking<Unit> {
        val value = Lottery(id = "stale")
        doReturn(completed(row(Lottery::class.java, value, "canonical"))).`when`(doc).get()
        val result = LotteryRepository().get("id")
        assertSame(value, result)
        assertEquals("canonical", result!!.id)
    }
    /**
     * Given a snapshot that cannot map to a model, expects a null result.
     */
    @Test fun getMissingDocumentReturnsNull() = runBlocking<Unit> {
        doReturn(completed(row(Lottery::class.java, null))).`when`(doc).get()
        assertNull(LotteryRepository().get("id"))
    }
    /**
     * Given a failed document read, expects the same exception to reach the caller.
     */
    @Test fun getPropagatesFailure() = runBlocking<Unit> {
        val failure = IllegalStateException("read failed")
        doReturn(failed<DocumentSnapshot>(failure)).`when`(doc).get()
        try { LotteryRepository().get("id"); fail("Expected read failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /**
     * Given a successful write, expects the generated document ID on the input model and as the return value.
     */
    @Test fun addAssignsAndReturnsGeneratedId() = runBlocking<Unit> {
        val value = Lottery(id = "previous")
        assertEquals("generated", LotteryRepository().add(value))
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
        val value = Lottery()
        val failure = IllegalStateException("write failed")
        doReturn(failed<Void>(failure)).`when`(generatedDoc).set(value)
        try { LotteryRepository().add(value); fail("Expected write failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /**
     * Given mapped and unmappable results from the open-lotteries query, expects only mapped records with snapshot IDs.
     */
    @Test fun listOpenMapsIdsAndSkipsNullObjects() = runBlocking<Unit> {
        val query = mock(Query::class.java)
        doReturn(query).`when`(collection).whereEqualTo("status", "open")
        val value = Lottery(id = "stale")
        doReturn(completed(snapshot(row(Lottery::class.java, null), row(Lottery::class.java, value, "canonical")))).`when`(query).get()
        val result = LotteryRepository().listOpen()
        assertEquals(listOf(value), result)
        assertEquals("canonical", result.single().id)
    }
    /**
     * Given no results from the open-lotteries query, expects an empty list.
     */
    @Test fun listOpenReturnsEmptyList() = runBlocking<Unit> {
        val query = mock(Query::class.java)
        doReturn(query).`when`(collection).whereEqualTo("status", "open")
        doReturn(completed(snapshot())).`when`(query).get()
        assertTrue(LotteryRepository().listOpen().isEmpty())
    }
    /**
     * Given failure of the open-lotteries query, expects the same exception to propagate.
     */
    @Test fun listOpenPropagatesQueryFailure() = runBlocking<Unit> {
        val query = mock(Query::class.java)
        doReturn(query).`when`(collection).whereEqualTo("status", "open")
        val failure = IllegalStateException("query failed")
        doReturn(failed<QuerySnapshot>(failure)).`when`(query).get()
        try { LotteryRepository().listOpen(); fail("Expected query failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /**
     * Given mapped and unmappable results from the owner-filtered query, expects only mapped records with snapshot IDs.
     */
    @Test fun listByOwnerMapsIdsAndSkipsNullObjects() = runBlocking<Unit> {
        val query = mock(Query::class.java)
        val owner = mock(DocumentReference::class.java)
        doReturn(query).`when`(collection).whereEqualTo("owner", owner)
        val value = Lottery(id = "stale")
        doReturn(completed(snapshot(row(Lottery::class.java, null), row(Lottery::class.java, value, "canonical")))).`when`(query).get()
        val result = LotteryRepository().listByOwner(owner)
        assertEquals(listOf(value), result)
        assertEquals("canonical", result.single().id)
    }
    /**
     * Given no results from the owner-filtered query, expects an empty list.
     */
    @Test fun listByOwnerReturnsEmptyList() = runBlocking<Unit> {
        val query = mock(Query::class.java)
        val owner = mock(DocumentReference::class.java)
        doReturn(query).`when`(collection).whereEqualTo("owner", owner)
        doReturn(completed(snapshot())).`when`(query).get()
        assertTrue(LotteryRepository().listByOwner(owner).isEmpty())
    }
    /**
     * Given failure of the owner-filtered query, expects the same exception to propagate.
     */
    @Test fun listByOwnerPropagatesQueryFailure() = runBlocking<Unit> {
        val query = mock(Query::class.java)
        val owner = mock(DocumentReference::class.java)
        doReturn(query).`when`(collection).whereEqualTo("owner", owner)
        val failure = IllegalStateException("query failed")
        doReturn(failed<QuerySnapshot>(failure)).`when`(query).get()
        try { LotteryRepository().listByOwner(owner); fail("Expected query failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /**
     * Given an existing model ID, expects a full document set using that model.
     */
    @Test fun updateReplacesDocument() = runBlocking<Unit> {
        val value = Lottery(id = "id")
        LotteryRepository().update(value)
        verify(doc).set(value)
    }
    /**
     * Given a failed replacement write, expects the same exception to propagate.
     */
    @Test fun updatePropagatesFailure() = runBlocking<Unit> {
        val value = Lottery(id = "id")
        val failure = IllegalStateException("write failed")
        doReturn(failed<Void>(failure)).`when`(doc).set(value)
        try { LotteryRepository().update(value); fail("Expected write failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /**
     * For each status, expects an update of the status field using its name in lower case.
     */
    @Test fun setStatusWritesEveryStatusInLowerCase() = runBlocking<Unit> {
        for (status in LotteryStatus.values()) {
            val stored = status.name.lowercase()
            doReturn(completed<Void?>(null)).`when`(doc).update("status", stored)
            LotteryRepository().setStatus("id", status.name)
            verify(doc).update("status", stored)
        }
    }
    /**
     * Given a failed status update, expects the same exception to propagate.
     */
    @Test fun setStatusPropagatesFailure() = runBlocking<Unit> {
        val status = "open"
        val failure = IllegalStateException("update failed")
        doReturn(failed<Void>(failure)).`when`(doc).update("status", status)
        try { LotteryRepository().setStatus("id", status); fail("Expected update failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /** Stubs the current lottery document’s entries subcollection. */
    private fun entries(): CollectionReference {
        val entries = mock(CollectionReference::class.java)
        doReturn(entries).`when`(doc).collection("entries")
        return entries
    }
    /** Stubs a readable lottery with the supplied waitlist capacity. */
    private fun lottery(capacity: Int?) {
        val now = Timestamp.now()
        doReturn(completed(row(Lottery::class.java, Lottery(
            owner = "owner", campsiteID = "site-1", winnerCount = 1,
            registrationStart = Timestamp(now.seconds - 86_400, now.nanoseconds),
            registrationEnd = Timestamp(now.seconds + 86_400, now.nanoseconds),
            status = "open", waitlistCap = (capacity ?: 0).toLong())))).`when`(doc).get()
    }
    /**
     * Given a missing lottery, expects false without accessing its entries collection.
     */
    @Test fun joinMissingLotteryReturnsFalse() = runBlocking<Unit> {
        doReturn(completed(row(Lottery::class.java, null))).`when`(doc).get()
        assertFalse(LotteryRepository().join("id", "entrant"))
        verify(doc, never()).collection("entries")
    }
    /**
     * Given one entry and capacity one, expects false without requesting an entrant document.
     */
    @Test fun joinAtCapacityReturnsFalseWithoutWritingEntry() = runBlocking<Unit> {
        lottery(1)
        val entries = entries()
        doReturn(completed(snapshot(row(LotteryEntry::class.java, LotteryEntry())))).`when`(entries).get()
        assertFalse(LotteryRepository().join("id", "entrant"))
        verify(entries, never()).document("entrant")
    }
    /**
     * Given a zero or negative capacity, which means no limit, expects the join to succeed.
     */
    @Test fun joinZeroOrNegativeCapacityMeansNoLimit() = runBlocking<Unit> {
        val entries = entries()
        val entryDoc = mock(DocumentReference::class.java)
        doReturn(entryDoc).`when`(entries).document("entrant")
        doReturn(completed(mock(DocumentSnapshot::class.java))).`when`(entryDoc).get() // not joined yet
        doReturn(completed<Void?>(null)).`when`(entryDoc).set(anyPayload())
        for (capacity in listOf(0, -1)) {
            lottery(capacity)
            assertTrue(LotteryRepository().join("id", "entrant"))
        }
    }
    /**
     * Given an entrant who is already on the waitlist, expects false without writing a second entry.
     */
    @Test fun joinAlreadyJoinedReturnsFalseWithoutWritingEntry() = runBlocking<Unit> {
        lottery(null)
        val entries = entries()
        val entryDoc = mock(DocumentReference::class.java)
        doReturn(entryDoc).`when`(entries).document("entrant")
        val existing = mock(DocumentSnapshot::class.java)
        doReturn(true).`when`(existing).exists()
        doReturn(completed(existing)).`when`(entryDoc).get()
        assertFalse(LotteryRepository().join("id", "entrant"))
        verify(entryDoc, never()).set(anyPayload())
    }
    /**
     * Given remaining capacity, expects a successful join and an entrant-keyed entry with a pending result.
     */
    @Test fun joinBelowCapacityWritesEntrantKeyAndPendingResult() = runBlocking<Unit> {
        lottery(2)
        val entries = entries()
        doReturn(completed(snapshot(row(LotteryEntry::class.java, LotteryEntry())))).`when`(entries).get()
        val entryDoc = mock(DocumentReference::class.java)
        doReturn(entryDoc).`when`(entries).document("entrant")
        doReturn(completed(mock(DocumentSnapshot::class.java))).`when`(entryDoc).get() // not joined yet
        doReturn(completed<Void?>(null)).`when`(entryDoc).set(anyPayload())
        assertTrue(LotteryRepository().join("id", "entrant"))
        val captor = ArgumentCaptor.forClass(LotteryEntry::class.java)
        verify(entryDoc).set(captor.capture() ?: LotteryEntry())
        assertEquals("entrant", captor.value.id)
        assertEquals("entrant", captor.value.entrantId)
        assertEquals(EntryResult.PENDING, captor.value.result)
    }
    /**
     * Given an open lottery with no capacity set, expects a successful entry write.
     */
    @Test fun unlimitedJoinAllowsEntry() = runBlocking<Unit> {
        lottery(null)
        val entries = entries()
        val entryDoc = mock(DocumentReference::class.java)
        doReturn(entryDoc).`when`(entries).document("entrant")
        doReturn(completed(mock(DocumentSnapshot::class.java))).`when`(entryDoc).get() // not joined yet
        doReturn(completed<Void?>(null)).`when`(entryDoc).set(anyPayload())
        assertTrue(LotteryRepository().join("id", "entrant"))
    }
    /**
     * Given an entry-write failure during an unlimited join, expects the same exception to propagate.
     */
    @Test fun joinPropagatesEntryWriteFailure() = runBlocking<Unit> {
        lottery(null)
        val entries = entries()
        val entryDoc = mock(DocumentReference::class.java)
        doReturn(entryDoc).`when`(entries).document("entrant")
        doReturn(completed(mock(DocumentSnapshot::class.java))).`when`(entryDoc).get() // not joined yet
        val failure = IllegalStateException("join write failed")
        doReturn(failed<Void>(failure)).`when`(entryDoc).set(anyPayload())
        try { LotteryRepository().join("id", "entrant"); fail("Expected join failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }
    /**
     * Given lottery and entrant IDs, expects deletion of the corresponding entry document.
     */
    @Test fun leaveDeletesEntrantDocument() = runBlocking<Unit> {
        val entries = entries()
        val entryDoc = mock(DocumentReference::class.java)
        doReturn(entryDoc).`when`(entries).document("entrant")
        doReturn(completed<Void?>(null)).`when`(entryDoc).delete()
        LotteryRepository().leave("id", "entrant")
        verify(entryDoc).delete()
    }
    /**
     * Given mapped and unmappable entry snapshots, expects only mapped entries with snapshot IDs.
     */
    @Test fun listEntriesMapsIdsAndSkipsNullObjects() = runBlocking<Unit> {
        val entries = entries()
        val value = LotteryEntry()
        doReturn(completed(snapshot(row(LotteryEntry::class.java, null), row(LotteryEntry::class.java, value, "canonical")))).`when`(entries).get()
        assertEquals(listOf(value), LotteryRepository().listEntries("id"))
        assertEquals("canonical", value.id)
    }
    /**
     * For existing and absent entry snapshots, expects the returned Boolean to match document existence.
     */
    @Test fun hasJoinedReturnsDocumentExistence() = runBlocking<Unit> {
        val entries = entries()
        val entryDoc = mock(DocumentReference::class.java)
        doReturn(entryDoc).`when`(entries).document("entrant")
        val snapshot = mock(DocumentSnapshot::class.java)
        doReturn(completed(snapshot)).`when`(entryDoc).get()
        for (exists in listOf(true, false)) {
            doReturn(exists).`when`(snapshot).exists()
            assertEquals(exists, LotteryRepository().hasJoined("id", "entrant"))
        }
    }
    /**
     * For each entry-result enum, expects an update of the result field using its name.
     */
    @Test fun setEntryResultWritesEveryEnumByName() = runBlocking<Unit> {
        val entries = entries()
        val entryDoc = mock(DocumentReference::class.java)
        doReturn(entryDoc).`when`(entries).document("entrant")
        for (result in EntryResult.values()) {
            doReturn(completed<Void?>(null)).`when`(entryDoc).update("result", result.name)
            LotteryRepository().setEntryResult("id", "entrant", result)
            verify(entryDoc).update("result", result.name)
        }
    }
    /** Rejects an already over-capacity list, catching equality-only capacity checks. */
    @Test fun joinAboveCapacityReturnsFalseWithoutWriting() = runBlocking<Unit> {
        lottery(1)
        val entries = entries()
        doReturn(completed(snapshot(
            row(LotteryEntry::class.java, LotteryEntry(entrantId = "a"), "a"),
            row(LotteryEntry::class.java, LotteryEntry(entrantId = "b"), "b")
        ))).`when`(entries).get()
        assertFalse(LotteryRepository().join("id", "entrant"))
        verify(entries, never()).document(anyString())
        verify(entries, never()).document()
    }

    /** Does not attempt any entry operation when loading the parent lottery fails. */
    @Test fun joinReadFailureDoesNotWriteEntry() = runBlocking<Unit> {
        val failure = IllegalStateException("lottery read failed")
        doReturn(failed<DocumentSnapshot>(failure)).`when`(doc).get()
        try { LotteryRepository().join("id", "entrant"); fail("Expected lottery read failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
        verify(doc, never()).collection("entries")
    }

    /** Does not write an entry when the limited waitlist cannot be read reliably. */
    @Test fun joinWaitlistReadFailureDoesNotWriteEntry() = runBlocking<Unit> {
        lottery(2)
        val entries = entries()
        val failure = IllegalStateException("waitlist read failed")
        doReturn(failed<QuerySnapshot>(failure)).`when`(entries).get()
        try { LotteryRepository().join("id", "entrant"); fail("Expected waitlist read failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
        verify(entries, never()).document(anyString())
        verify(entries, never()).document()
    }

    /** Propagates a failed leave operation rather than reporting that the entrant was removed. */
    @Test fun leavePropagatesFailure() = runBlocking<Unit> {
        val entries = entries()
        val entryDoc = mock(DocumentReference::class.java)
        doReturn(entryDoc).`when`(entries).document("entrant")
        val failure = IllegalStateException("leave failed")
        doReturn(failed<Void>(failure)).`when`(entryDoc).delete()
        try { LotteryRepository().leave("id", "entrant"); fail("Expected leave failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /** Maps an empty waitlist to an empty result. */
    @Test fun listEntriesReturnsEmptyList() = runBlocking<Unit> {
        val entries = entries()
        doReturn(completed(snapshot())).`when`(entries).get()
        assertTrue(LotteryRepository().listEntries("id").isEmpty())
    }

    /** Propagates failure when checking whether an entrant has joined. */
    @Test fun hasJoinedPropagatesFailure() = runBlocking<Unit> {
        val entries = entries()
        val entryDoc = mock(DocumentReference::class.java)
        doReturn(entryDoc).`when`(entries).document("entrant")
        val failure = IllegalStateException("membership read failed")
        doReturn(failed<DocumentSnapshot>(failure)).`when`(entryDoc).get()
        try { LotteryRepository().hasJoined("id", "entrant"); fail("Expected membership failure") }
        catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

    /** Propagates a failed draw-result update. */
    @Test fun setEntryResultPropagatesFailure() = runBlocking<Unit> {
        val entries = entries()
        val entryDoc = mock(DocumentReference::class.java)
        doReturn(entryDoc).`when`(entries).document("entrant")
        val failure = IllegalStateException("result write failed")
        doReturn(failed<Void>(failure)).`when`(entryDoc).update("result", EntryResult.WON.name)
        try {
            LotteryRepository().setEntryResult("id", "entrant", EntryResult.WON)
            fail("Expected result failure")
        } catch (actual: IllegalStateException) { assertSame(failure, actual) }
    }

}
