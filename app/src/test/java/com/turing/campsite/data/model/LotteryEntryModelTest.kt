package com.turing.campsite.data.model

import com.google.firebase.Timestamp
import org.junit.Assert.*
import org.junit.Test

/**
 * Checks entry defaults and independent identity and result fields in memory.
 *
 * Exercises only model behavior; no persistence, UI, or server authorization is tested.
 */
class LotteryEntryModelTest {
    private val fixed = Timestamp(1_000L, 123)
    private val later = Timestamp(2_000L, 456)

    /** Verifies that a new entry has empty identifiers, a PENDING result, and the supplied join time. */
    @Test fun newEntryIsPendingWithEmptyIdentity() {
        val subject = LotteryEntry(joinedAt = fixed)
        assertEquals("", subject.id)
        assertEquals("", subject.entrantId)
        assertEquals(EntryResult.PENDING, subject.result)
        assertEquals(fixed, subject.joinedAt)
    }

    /** Verifies that assigning each result leaves entry identity and join time unchanged. */
    @Test fun everyResultCanBeStoredWithoutChangingEntryIdentity() {
        val subject = LotteryEntry(id = "entrant-1", entrantId = "entrant-1", joinedAt = fixed)
        for (result in EntryResult.values()) {
            subject.result = result
            assertEquals(result, subject.result)
            assertEquals("entrant-1", subject.id)
            assertEquals("entrant-1", subject.entrantId)
            assertEquals(fixed, subject.joinedAt)
        }
    }

    /** Verifies that a WON result can be replaced with DECLINED without changing the entrant identifier. */
    @Test fun winningEntryCanRecordDeclinedResult() {
        val subject = LotteryEntry(entrantId = "entrant-1", joinedAt = fixed, result = EntryResult.WON)
        subject.result = EntryResult.DECLINED
        assertEquals(EntryResult.DECLINED, subject.result)
        assertEquals("entrant-1", subject.entrantId)
    }

    /** Verifies that distinct entry and entrant identifiers are retained without enforcing equality. */
    @Test fun entryIdAndEntrantIdAreStoredIndependently() {
        // Matching these fields is a repository convention, not model validation.
        val subject = LotteryEntry(id = "document-1", entrantId = "entrant-1", joinedAt = fixed)
        assertEquals("document-1", subject.id)
        assertEquals("entrant-1", subject.entrantId)
    }

}
