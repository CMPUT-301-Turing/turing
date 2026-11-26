package com.turing.campsite.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.turing.campsite.data.model.Message
import com.turing.campsite.data.model.Notification
import com.turing.campsite.data.model.User
import com.turing.campsite.data.model.UserRole
import com.turing.campsite.data.repository.MessageRepository
import com.turing.campsite.data.repository.NotificationRepository
import com.turing.campsite.data.repository.ReservationRepository
import com.turing.campsite.data.repository.UserRepository
import com.turing.campsite.ui.screens.admin.canRemoveOwner
import com.turing.campsite.ui.screens.admin.ownerOutgoingMessages

/**
 * Loads the admin audit and moderation data for one destination and account.
 */
internal class AdminSection6ViewModel : BaseViewModel() {
    private val userRepository by lazy { UserRepository() }
    private val notificationRepository by lazy { NotificationRepository() }
    private val messageRepository by lazy { MessageRepository() }
    private val reservationRepository by lazy { ReservationRepository() }

    var notifications by mutableStateOf<List<Notification>>(emptyList())
        private set
    var messages by mutableStateOf<List<Message>>(emptyList())
        private set
    var users by mutableStateOf<List<User>>(emptyList())
        private set
    var isAuthorized by mutableStateOf(false)
        private set
    var statusMessage by mutableStateOf<String?>(null)
        private set

    /** Loads stored notifications after verifying the account's administrator role. */
    fun loadNotifications(uid: String) = loadForAdmin(uid) {
        notifications = notificationRepository.listAll()
    }

    /** Resolves each distinct reservation once and keeps only its owner-to-entrant messages. */
    fun loadMessages(uid: String) = loadForAdmin(uid) {
        val allMessages = messageRepository.listAll()
        val reservations = allMessages.map { it.reservationId }
            .filter { it.isNotBlank() }
            .distinct()
            .mapNotNull { id -> reservationRepository.get(id)?.let { id to it } }
            .toMap()
        messages = ownerOutgoingMessages(allMessages, reservations)
    }

    /** Shows the same audit direction for one reservation, not its full two-way conversation. */
    fun loadConversation(uid: String, reservationId: String) = loadForAdmin(uid) {
        val reservation = if (reservationId.isBlank()) null else reservationRepository.get(reservationId)
        messages = if (reservation == null) {
            emptyList()
        } else {
            ownerOutgoingMessages(
                messageRepository.listForReservation(reservationId),
                mapOf(reservationId to reservation)
            )
        }
    }

    /** Loads profiles for moderation; role eligibility is checked again before a write. */
    fun loadProfiles(uid: String) = loadForAdmin(uid) {
        users = userRepository.listAll()
    }

    fun removeOwner(uid: String, profile: User) {
        if (!isAuthorized || isLoading || !canRemoveOwner(profile, uid)) return
        statusMessage = null
        launchCall {
            userRepository.removeOwnerAccess(profile.id, uid)
            users = users.map { user ->
                if (user.id == profile.id) user.copy(roles = user.roles - UserRole.OWNER) else user
            }
            statusMessage = "Owner access removed."
        }
    }

    private fun loadForAdmin(uid: String, fetch: suspend () -> Unit) {
        isAuthorized = false
        statusMessage = null
        notifications = emptyList()
        messages = emptyList()
        users = emptyList()
        launchCall {
            if (userRepository.get(uid)?.hasRole(UserRole.ADMIN) != true) {
                statusMessage = "Administrator access required."
                return@launchCall
            }
            fetch()
            isAuthorized = true
        }
    }
}
