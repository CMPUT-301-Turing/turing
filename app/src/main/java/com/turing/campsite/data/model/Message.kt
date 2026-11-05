package com.turing.campsite.data.model

import com.google.firebase.Timestamp

/**
 * Represents one chat message between an owner and an entrant.
 *
 * Messages are always about a single reservation, and admins can read every
 * message for the log.
 *
 * @property id the Firestore document id
 * @property reservationId the id of the reservation being discussed
 * @property fromUserId the uid of the sender
 * @property toUserId the uid of the receiver
 * @property body the message text
 * @property createdAt when the message was sent
 */
data class Message(
    var id: String = "",
    var reservationId: String = "",
    var fromUserId: String = "",
    var toUserId: String = "",
    var body: String = "",
    var createdAt: Timestamp = Timestamp.now()
)
