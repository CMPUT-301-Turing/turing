package com.turing.campsite.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.turing.campsite.data.model.Message
import com.turing.campsite.data.repository.MessageRepository

/**
 * Holds chat messages for the owner message screen and the admin message log.
 *
 * Screens call one of the load functions and read messages.
 *
 * @property messages the list being shown
 */
class MessageViewModel(
    private val repository: MessageRepository = MessageRepository()
) : BaseViewModel() {

    var messages by mutableStateOf<List<Message>>(emptyList())
        private set

    /**
     * Loads the chat for one reservation, oldest first.
     *
     * @param reservationId the Firestore document id of the reservation
     */
    fun loadForReservation(reservationId: String) {
        launchCall {
            messages = repository.listForReservation(reservationId)
        }
    }

    /**
     * Loads every message, newest first, for the admin log.
     */
    fun loadAll() {
        launchCall {
            messages = repository.listAll()
        }
    }

    /**
     * Sends a message and adds it to the end of messages.
     *
     * @param message the message to send; its id is filled in by the repository
     */
    fun send(message: Message) {
        launchCall {
            repository.add(message)
            messages = messages + message
        }
    }
}
