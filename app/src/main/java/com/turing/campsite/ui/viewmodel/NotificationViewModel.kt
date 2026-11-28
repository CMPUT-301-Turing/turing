package com.turing.campsite.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.turing.campsite.data.model.Notification
import com.turing.campsite.data.repository.NotificationRepository
import com.turing.campsite.data.repository.UserRepository

/**
 * Holds notification data for the entrant inbox, the owner notification
 * screen and the admin notification log.
 *
 * Screens call one of the load functions and read notifications.
 *
 * Sending goes through here so users who opted out of notifications
 * (US 01.10.01) are skipped.
 *
 * @property notifications the list being shown, newest first
 * @property unreadCount how many notifications in the list are not read yet
 */
class NotificationViewModel(
    private val repository: NotificationRepository = NotificationRepository(),
    private val userRepository: UserRepository = UserRepository()
) : BaseViewModel() {

    var notifications by mutableStateOf<List<Notification>>(emptyList())
        private set

    val unreadCount: Int
        get() = notifications.count { !it.read }

    /**
     * Loads the notifications addressed to one user.
     *
     * @param userId the uid of the receiving user
     */
    fun loadForUser(userId: String) {
        launchCall {
            notifications = repository.listForUser(userId)
        }
    }

    /**
     * Loads the notifications one user has sent, for the owner screen.
     *
     * @param userId the uid of the sending user
     */
    fun loadSentBy(userId: String) {
        launchCall {
            notifications = repository.listSentBy(userId)
        }
    }

    /**
     * Loads every notification, for the admin log.
     */
    fun loadAll() {
        launchCall {
            notifications = repository.listAll()
        }
    }

    /**
     * Sends one notification, unless the receiver opted out of notifications.
     *
     * @param notification the notification to send
     */
    fun send(notification: Notification) {
        launchCall {
            if (wantsNotifications(notification.toUserId)) {
                repository.add(notification)
            }
        }
    }

    /**
     * Sends the same notification to several users, for example a waitlist.
     *
     * Users who opted out of notifications are left out.
     *
     * @param userIds the uids of the receiving users
     * @param notification the notification to copy to each user
     */
    fun sendToMany(userIds: List<String>, notification: Notification) {
        launchCall {
            val receivers = userIds.filter { wantsNotifications(it) }
            if (receivers.isNotEmpty()) {
                repository.sendToMany(receivers, notification)
            }
        }
    }

    /**
     * Checks whether a user still wants notifications.
     *
     * A user without a profile document is treated as opted in, since
     * notificationsEnabled is true by default.
     *
     * @param uid the uid of the user to check
     * @return false only if the user turned notifications off
     */
    private suspend fun wantsNotifications(uid: String): Boolean {
        return userRepository.get(uid)?.notificationsEnabled != false
    }

    /**
     * Marks a notification as read and updates it in notifications.
     *
     * @param notificationId the Firestore document id of the notification
     */
    fun markRead(notificationId: String) {
        launchCall {
            repository.markRead(notificationId)
            notifications = notifications.map {
                if (it.id == notificationId) it.copy(read = true) else it
            }
        }
    }
}
