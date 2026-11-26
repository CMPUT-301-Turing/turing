/* Checks that audits include only reservation-linked owner-to-entrant messages and eligible moderation targets. */
package com.turing.campsite.ui.screens.admin

import com.turing.campsite.data.model.Message
import com.turing.campsite.data.model.Reservation
import com.turing.campsite.data.model.User
import com.turing.campsite.data.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdminAuditSelectionTest {
    @Test
    fun onlyOwnerToReservedEntrantMatches() {
        val a = Reservation(id = "r1", ownerId = "owner-1", entrantId = "entrant-1")
        val b = Reservation(id = "r2", ownerId = "owner-2", entrantId = "entrant-2")
        val valid = Message(id = "good", reservationId = "r1", fromUserId = a.ownerId, toUserId = a.entrantId)
        val reverse = valid.copy(id = "reverse", fromUserId = a.entrantId, toUserId = a.ownerId)
        val wrongBooking = valid.copy(id = "wrong", reservationId = "r2")
        val orphan = valid.copy(id = "missing", reservationId = "deleted")
        val unrelatedRecipient = valid.copy(id = "unrelated", toUserId = "entrant-2")
        val selfBooking = a.copy(id = "r3", entrantId = a.ownerId)
        val selfMessage = valid.copy(id = "self", reservationId = "r3", toUserId = a.ownerId)
        val messages = listOf(valid, reverse, wrongBooking, orphan, unrelatedRecipient, selfMessage)
        assertEquals(listOf(valid), ownerOutgoingMessages(messages, mapOf("r1" to a, "r2" to b, "r3" to selfBooking)))
    }

    @Test
    fun ownerRemovalProtectsSelfAdminNonOwnerAndBlankId() {
        val owner = User(id = "owner-1")
        assertTrue(canRemoveOwner(owner, "admin-1"))
        assertFalse(canRemoveOwner(owner, "owner-1"))
        assertFalse(canRemoveOwner(owner.copy(roles = listOf(UserRole.OWNER, UserRole.ADMIN)), "admin-1"))
        assertFalse(canRemoveOwner(owner.copy(roles = listOf(UserRole.ENTRANT)), "admin-1"))
        assertFalse(canRemoveOwner(owner.copy(id = ""), "admin-1"))
    }
}
