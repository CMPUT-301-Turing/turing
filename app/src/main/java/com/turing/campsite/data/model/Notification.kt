package com.turing.campsite.data.model

import com.google.firebase.Timestamp

/**
 * The kinds of notification the app can send.
 *
 * The first four are sent by the system when a lottery or reservation
 * changes. OWNER_MESSAGE is written by an owner to their entrants.
 */
enum class NotificationType {
    LOTTERY_WON, LOTTERY_LOST, RESERVATION_CONFIRMED, RESERVATION_CANCELLED, OWNER_MESSAGE
}

/**
 * Represents one document in the "notifications" collection.
 *
 * Each notification is addressed to a single user. Sending the same text to
 * a whole waitlist creates one document per user.
 *
 * @property id the Firestore document id
 * @property toUserId the uid of the user who receives the notification
 * @property fromUserId the uid of the sender, or empty for system notifications
 * @property type what kind of notification this is
 * @property title the short heading shown in the list
 * @property body the full message text
 * @property read whether the user has opened it
 * @property createdAt when the notification was sent
 */
data class Notification(
    var id: String = "",
    var toUserId: String = "",
    var fromUserId: String = "",
    var type: NotificationType = NotificationType.OWNER_MESSAGE,
    var title: String = "",
    var body: String = "",
    var read: Boolean = false,
    var createdAt: Timestamp = Timestamp.now()
)
