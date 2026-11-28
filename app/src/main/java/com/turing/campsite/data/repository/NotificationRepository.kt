/* AI Assistance Disclosure: OpenAI, ChatGPT (GPT-6), 2026-11-26.
 * Scope: Campsite cascade integration and required ownerId compatibility updates.
 */
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

    /** Reads the campsite checkpoint, not another user's private inbox. */
    suspend fun sendCampsiteRemoval(campsiteId: String, notification: Notification) {
        val db = FirebaseFirestore.getInstance()
        val campsiteRef = db.collection("campsites").document(campsiteId)
        val key = java.security.MessageDigest.getInstance("SHA-256")
            .digest("$campsiteId:${notification.toUserId}".toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
        val notificationRef = notifications.document("campsite_removed_$key")
        db.runTransaction { tx ->
            val snapshot = tx.get(campsiteRef)
            check(snapshot.getBoolean("deletion.recipientsCaptured") == true) {
                "Deletion recipients have not been captured"
            }
            val recipients = snapshot.get("deletion.recipientIds") as? List<*> ?: emptyList<Any>()
            check(notification.toUserId in recipients) { "Recipient is not in the deletion snapshot" }
            val recipientGate = tx.get(db.collection("deletedUsers").document(notification.toUserId))
            val recipientProfile = tx.get(db.collection("users").document(notification.toUserId))
            val delivered = snapshot.get("deletion.notifiedRecipientIds") as? List<*> ?: emptyList<Any>()
            if (notification.toUserId !in delivered) {
                if (!recipientGate.exists() && recipientProfile.exists() &&
                    recipientProfile.getBoolean("notificationsEnabled") != false) {
                    tx.set(notificationRef, notification.copy(id = notificationRef.id))
                }
                tx.update(campsiteRef, "deletion.notifiedRecipientIds",
                    com.google.firebase.firestore.FieldValue.arrayUnion(notification.toUserId))
            }
        }.await()
    }

    /** Historical sent/received records are removed; system removal notices have no user sender. */
    suspend fun deleteForUser(uid: String) {
        for (field in listOf("fromUserId", "toUserId")) {
            while (true) {
                val docs = notifications.whereEqualTo(field, uid).limit(400)
                    .get(com.google.firebase.firestore.Source.SERVER).await().documents
                if (docs.isEmpty()) break
                val batch = FirebaseFirestore.getInstance().batch()
                docs.forEach { batch.delete(it.reference) }; batch.commit().await()
            }
        }
    }

    suspend fun sendUserRemoval(uid: String, notification: Notification) {
        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("deletedUsers").document(uid)
        val key = java.security.MessageDigest.getInstance("SHA-256")
            .digest("$uid:${notification.toUserId}".toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }
        val notificationRef = notifications.document("user_removed_$key")
        db.runTransaction { tx ->
            val doc = tx.get(ref)
            check(doc.getString("status") == "DELETING" && doc.getBoolean("planCaptured") == true)
            val recipients = doc.get("recipientIds") as? List<*> ?: error("Missing deletion recipients")
            check(notification.toUserId in recipients)
            val gate = tx.get(db.collection("deletedUsers").document(notification.toUserId))
            val profile = tx.get(db.collection("users").document(notification.toUserId))
            val delivered = doc.get("notifiedRecipientIds") as? List<*> ?: emptyList<Any>()
            if (notification.toUserId !in delivered) {
                if (!gate.exists() && profile.exists() && profile.getBoolean("notificationsEnabled") != false) {
                    tx.set(notificationRef, notification.copy(id = notificationRef.id))
                }
                tx.update(ref, "notifiedRecipientIds", com.google.firebase.firestore.FieldValue.arrayUnion(notification.toUserId))
            }
        }.await()
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
