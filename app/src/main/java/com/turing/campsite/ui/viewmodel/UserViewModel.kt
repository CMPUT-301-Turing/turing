package com.turing.campsite.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.turing.campsite.data.model.User
import com.turing.campsite.data.repository.UserRepository

/**
 * Holds user data for the profile screens and the admin profile list.
 *
 * Screens read user and users, and call the functions here instead of using
 * UserRepository directly.
 *
 * @property user the user whose profile is being shown, or null before load()
 * @property users every user, filled by loadAll() for the admin screens
 */
class UserViewModel(
    private val repository: UserRepository = UserRepository()
) : BaseViewModel() {

    var user by mutableStateOf<User?>(null)
        private set

    var users by mutableStateOf<List<User>>(emptyList())
        private set

    /**
     * Loads one user's profile into user.
     *
     * @param uid the uid of the user to load
     */
    fun load(uid: String) {
        launchCall {
            user = repository.get(uid)
        }
    }

    /**
     * Creates or replaces a user document and shows it as the current user.
     *
     * @param newUser the user to save; its id must already be set to the uid
     */
    fun save(newUser: User) {
        launchCall {
            repository.save(newUser)
            user = newUser
        }
    }

    /**
     * Updates the profile fields of a user.
     *
     * @param uid the uid of the user to update
     * @param name the new display name
     * @param email the new contact email
     * @param phone the new contact phone number
     */
    fun updateProfile(uid: String, name: String, email: String, phone: String) {
        launchCall {
            repository.updateProfile(uid, name, email, phone)
            user = user?.copy(name = name, email = email, phone = phone)
        }
    }

    /**
     * Turns notifications on or off for a user.
     *
     * @param uid the uid of the user to update
     * @param enabled true to receive notifications, false to opt out
     */
    fun setNotificationsEnabled(uid: String, enabled: Boolean) {
        launchCall {
            repository.setNotificationsEnabled(uid, enabled)
            user = user?.copy(notificationsEnabled = enabled)
        }
    }

    /**
     * Loads every user into users, for the admin screens.
     */
    fun loadAll() {
        launchCall {
            users = repository.listAll()
        }
    }

    /**
     * Deletes a user and removes them from users.
     *
     * @param uid the uid of the user to delete
     */
    fun delete(uid: String) {
        launchCall {
            repository.delete(uid)
            users = users.filter { it.id != uid }
            if (user?.id == uid) user = null
        }
    }
}
