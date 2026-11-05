package com.turing.campsite.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.turing.campsite.data.model.Notification
import kotlinx.coroutines.tasks.await

/**
 * Reads and writes the "notifications" collection in Firestore.
 *
 * Entrants read their inbox from it, owners send messages to their
 * waitlists through it, and admins read the full log.
 */
class NotificationRepository {
    private val notifications = FirebaseFirestore.getInstance().collection("notifications")

    /**
     * Loads the notifications addressed to one user, newest first.
     *
     * @param userId the uid of the receiving user
     * @return the notifications whose toUserId matches
     */
    suspend fun listForUser(userId: String): List<Notification> {
        val snapshot = notifications.whereEqualTo("toUserId", userId).get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Notification::class.java)?.also { it.id = doc.id }
        }.sortedByDescending { it.createdAt }
    }

    /**
     * Loads the notifications one user has sent, newest first.
     *
     * @param userId the uid of the sending user
     * @return the notifications whose fromUserId matches
     */
    suspend fun listSentBy(userId: String): List<Notification> {
        val snapshot = notifications.whereEqualTo("fromUserId", userId).get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Notification::class.java)?.also { it.id = doc.id }
        }.sortedByDescending { it.createdAt }
    }

    /**
     * Loads every notification, newest first, for the admin log.
     *
     * @return all notifications in the collection
     */
    suspend fun listAll(): List<Notification> {
        val snapshot = notifications.get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Notification::class.java)?.also { it.id = doc.id }
        }.sortedByDescending { it.createdAt }
    }

    /**
     * Sends one notification with a generated id.
     *
     * @param notification the notification to send; its id is filled in by this call
     * @return the generated document id
     */
    suspend fun add(notification: Notification): String {
        val doc = notifications.document()
        notification.id = doc.id
        doc.set(notification).await()
        return doc.id
    }

    /**
     * Sends the same notification to several users.
     *
     * One document is written per user, for example when an owner messages
     * everyone on a waitlist.
     *
     * @param userIds the uids of the receiving users
     * @param notification the notification to copy to each user
     */
    suspend fun sendToMany(userIds: List<String>, notification: Notification) {
        for (uid in userIds) {
            add(notification.copy(toUserId = uid))
        }
    }

    /**
     * Marks a notification as opened by the user.
     *
     * @param id the Firestore document id of the notification
     */
    suspend fun markRead(id: String) {
        notifications.document(id).update("read", true).await()
    }
}
