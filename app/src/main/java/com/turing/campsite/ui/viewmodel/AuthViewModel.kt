/* AI Assistance Disclosure: OpenAI, ChatGPT (GPT-6), 2026-11-26.
 * Scope: Role-aware view switching using the signed-in profile and shared navigation guards.
 */
package com.turing.campsite.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.turing.campsite.data.auth.DeviceAuthManager
import com.turing.campsite.data.model.User
import com.turing.campsite.data.repository.UserRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

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

    var currentUser by mutableStateOf<User?>(null)
        private set

    var isProfileLoading by mutableStateOf(true)
        private set

    var profileErrorMessage by mutableStateOf<String?>(null)
        private set

    private var profileObservation: Job? = null

    /** Start or retry the profile listener; never sign out, recreate a profile, or change roles. */
    fun observeCurrentUser() {
        val signedInUid = uid ?: return
        profileObservation?.cancel()
        currentUser = null
        profileErrorMessage = null
        isProfileLoading = true
        profileObservation = viewModelScope.launch {
            try {
                userRepository.observe(signedInUid).collect { user ->
                    currentUser = user
                    isProfileLoading = false
                    profileErrorMessage = if (user == null) "Your profile could not be found" else null
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (failure: Exception) {
                currentUser = null
                isProfileLoading = false
                profileErrorMessage = failure.message ?: "Your profile could not be loaded"
            }
        }
    }

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
