package com.turing.campsite.data.model

import com.google.firebase.Timestamp
import org.junit.Assert.*
import org.junit.Test

/**
 * Checks notification defaults, types, read flags, and exact text retention in memory.
 *
 * Exercises only model behavior; no persistence, UI, or server authorization is tested.
 */
class NotificationModelTest {
    private val fixed = Timestamp(1_000L, 123)
    private val later = Timestamp(2_000L, 456)

    /** Verifies empty text and identifiers, OWNER_MESSAGE type, unread state, and the supplied creation time. */
    @Test fun defaultsAreUnreadOwnerMessageWithEmptyTextAndAddresses() {
        val subject = Notification(createdAt = fixed)
        assertEquals("", subject.id)
        assertEquals("", subject.toUserId)
        assertEquals("", subject.fromUserId)
        assertEquals(NotificationType.OWNER_MESSAGE, subject.type)
        assertEquals("", subject.title)
        assertEquals("", subject.body)
        assertFalse(subject.read)
        assertEquals(fixed, subject.createdAt)
    }

    /** Verifies for every notification type that marking it read leaves all other fields unchanged. */
    @Test fun everyNotificationTypeRetainsAddressAndContentWhenRead() {
        for (type in NotificationType.values()) {
            val subject = Notification(id = "notice-1", toUserId = "entrant-1", fromUserId = "owner-1",
                type = type, title = "Update", body = "Booking update", createdAt = fixed)
            val original = subject.copy()
            subject.read = true
            assertTrue(subject.read)
            assertEquals(type, subject.type)
            assertEquals(original, subject.copy(read = false))
        }
    }

    /** Verifies that a LOTTERY_WON notification retains its recipient while its omitted sender remains empty. */
    @Test fun systemNotificationCanHaveNoSender() {
        val subject = Notification(toUserId = "entrant-1", type = NotificationType.LOTTERY_WON, createdAt = fixed)
        assertEquals("", subject.fromUserId)
        assertEquals("entrant-1", subject.toUserId)
        assertEquals(NotificationType.LOTTERY_WON, subject.type)
    }

    /** Verifies that resetting the read flag leaves the title and body unchanged. */
    @Test fun readFlagCanBeResetWithoutChangingContent() {
        val subject = Notification(title = "Title", body = "Body", read = true, createdAt = fixed)
        subject.read = false
        assertFalse(subject.read)
        assertEquals("Title", subject.title)
        assertEquals("Body", subject.body)
    }

    /** Verifies exact preservation of Unicode and surrounding whitespace in titles and line breaks in bodies. */
    @Test fun contentRetainsUnicodeWhitespaceAndNewlines() {
        val subject = Notification(title = "  Café 🌲  ", body = "Line one\nLine two", createdAt = fixed)
        assertEquals("  Café 🌲  ", subject.title)
        assertEquals("Line one\nLine two", subject.body)
    }

}
