/* AI Assistance Disclosure: OpenAI, ChatGPT (GPT-6), 2026-11-26.
 * Scope: Campsite cascade integration and required ownerId compatibility updates.
 */
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

    /** Keeps the reservation in place until its messages have all been removed. */
    suspend fun deleteForReservation(reservationId: String) {
        while (true) {
            val documents = messages.whereEqualTo("reservationId", reservationId).limit(400)
                .get(com.google.firebase.firestore.Source.SERVER).await().documents
            if (documents.isEmpty()) return
            val batch = FirebaseFirestore.getInstance().batch()
            documents.forEach { batch.delete(it.reference) }
            batch.commit().await()
        }
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
