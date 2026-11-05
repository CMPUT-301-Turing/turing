package com.turing.campsite.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.turing.campsite.data.model.Message
import kotlinx.coroutines.tasks.await

/**
 * Reads and writes the "messages" collection in Firestore.
 *
 * Owners and entrants chat about a reservation through it, and admins can
 * read every message for the log.
 */
class MessageRepository {
    private val messages = FirebaseFirestore.getInstance().collection("messages")

    /**
     * Loads the chat for one reservation, oldest first.
     *
     * @param reservationId the Firestore document id of the reservation
     * @return the messages whose reservationId matches
     */
    suspend fun listForReservation(reservationId: String): List<Message> {
        val snapshot = messages.whereEqualTo("reservationId", reservationId).get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Message::class.java)?.also { it.id = doc.id }
        }.sortedBy { it.createdAt }
    }

    /**
     * Loads every message, newest first, for the admin log.
     *
     * @return all messages in the collection
     */
    suspend fun listAll(): List<Message> {
        val snapshot = messages.get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Message::class.java)?.also { it.id = doc.id }
        }.sortedByDescending { it.createdAt }
    }

    /**
     * Sends a message with a generated id.
     *
     * @param message the message to send; its id is filled in by this call
     * @return the generated document id
     */
    suspend fun add(message: Message): String {
        val doc = messages.document()
        message.id = doc.id
        doc.set(message).await()
        return doc.id
    }
}
