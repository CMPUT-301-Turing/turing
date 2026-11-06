package com.turing.campsite.data.model

import com.google.firebase.Timestamp
import org.junit.Assert.*
import org.junit.Test

/**
 * Checks message defaults, addressing, text preservation, and in-memory body edits.
 *
 * Exercises only model behavior; no persistence, UI, or server authorization is tested.
 */
class MessageModelTest {
    private val fixed = Timestamp(1_000L, 123)
    private val later = Timestamp(2_000L, 456)

    /** Verifies empty message content and identifiers with the supplied creation time. */
    @Test fun defaultsHaveNoContentOrConversationIdentity() {
        val subject = Message(createdAt = fixed)
        assertEquals("", subject.id)
        assertEquals("", subject.reservationId)
        assertEquals("", subject.fromUserId)
        assertEquals("", subject.toUserId)
        assertEquals("", subject.body)
        assertEquals(fixed, subject.createdAt)
    }

    /** Verifies that construction retains message identity, reservation, sender, recipient, body, and timestamp. */
    @Test fun constructorRetainsConversationAndDirection() {
        val subject = Message("message-1", "reservation-1", "owner-1", "entrant-1", "Arrival instructions", fixed)
        assertEquals("message-1", subject.id)
        assertEquals("reservation-1", subject.reservationId)
        assertEquals("owner-1", subject.fromUserId)
        assertEquals("entrant-1", subject.toUserId)
        assertEquals("Arrival instructions", subject.body)
        assertEquals(fixed, subject.createdAt)
    }

    /** Verifies that a separately constructed reply retains the reservation with reversed addresses and its own body and time. */
    @Test fun replyCanReverseSenderAndRecipientWithinSameReservation() {
        val outgoing = Message(reservationId = "reservation-1", fromUserId = "owner-1", toUserId = "entrant-1", createdAt = fixed)
        val reply = Message(reservationId = outgoing.reservationId, fromUserId = outgoing.toUserId,
            toUserId = outgoing.fromUserId, body = "Thank you", createdAt = later)
        assertEquals("reservation-1", reply.reservationId)
        assertEquals("entrant-1", reply.fromUserId)
        assertEquals("owner-1", reply.toUserId)
        assertEquals("Thank you", reply.body)
        assertEquals(later, reply.createdAt)
    }

    /** Verifies that the message body preserves Unicode, surrounding whitespace, and line breaks exactly. */
    @Test fun bodyPreservesUnicodeWhitespaceAndMultilineText() {
        val text = "  Café 🌲\nGate opens at 8.  "
        assertEquals(text, Message(body = text, createdAt = fixed).body)
    }

    /** Verifies that editing the body leaves every other message field unchanged. */
    @Test fun changingBodyDoesNotReaddressMessage() {
        val subject = Message(id = "message-1", reservationId = "reservation-1", fromUserId = "owner-1", toUserId = "entrant-1", createdAt = fixed)
        val original = subject.copy()
        subject.body = "Updated instructions"
        assertEquals("Updated instructions", subject.body)
        assertEquals(original, subject.copy(body = ""))
    }

}
