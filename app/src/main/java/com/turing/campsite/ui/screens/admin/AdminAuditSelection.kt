package com.turing.campsite.ui.screens.admin

import com.turing.campsite.data.model.Message
import com.turing.campsite.data.model.Reservation
import com.turing.campsite.data.model.User
import com.turing.campsite.data.model.UserRole

/**
 * Selects outgoing owner messages only when their sender and recipient match the booking.
 * Reservation participants, not a user's current roles, define the audit direction.
 */
internal fun ownerOutgoingMessages(
    messages: List<Message>,
    reservations: Map<String, Reservation>
): List<Message> = messages.filter { message ->
    val reservation = reservations[message.reservationId]
    reservation != null &&
        reservation.ownerId.isNotBlank() &&
        reservation.entrantId.isNotBlank() &&
        reservation.ownerId != reservation.entrantId &&
        message.fromUserId == reservation.ownerId &&
        message.toUserId == reservation.entrantId
}

/** Protects the acting admin and other admins, including profiles that also hold OWNER. */
internal fun canRemoveOwner(profile: User, adminId: String): Boolean =
    profile.id.isNotBlank() && profile.id != adminId &&
        profile.hasRole(UserRole.OWNER) && !profile.hasRole(UserRole.ADMIN)
