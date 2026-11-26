package com.turing.campsite.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.turing.campsite.data.auth.DeviceAuthManager
import com.turing.campsite.data.model.User
import com.turing.campsite.data.repository.UserRepository

/**
 * Signs the phone in when the app starts.
 *
 * The phone is signed in with Firebase Anonymous Auth, so the user never
 * types a username or password. The first time a phone signs in, a document
 * is created for it in the "users" collection.
 *
 * @property uid the uid of the signed-in phone, or null until signIn() has finished
 */
class AuthViewModel(
    private val authManager: DeviceAuthManager = DeviceAuthManager(),
    private val userRepository: UserRepository = UserRepository()
) : BaseViewModel() {

    var uid by mutableStateOf<String?>(null)
        private set

    /**
     * Signs the phone in and makes sure it has a user document.
     *
     * Does nothing if the phone is already signed in or a sign-in is running.
     * If it fails, errorMessage is set and signIn() can be called again.
     */
    fun signIn() {
        if (uid != null || isLoading) return
        launchCall {
            val signedInUid = authManager.signIn()
            if (userRepository.get(signedInUid) == null) {
                userRepository.save(User(id = signedInUid))
            }
            uid = signedInUid
        }
    }
}
