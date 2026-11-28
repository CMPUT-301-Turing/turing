package com.turing.campsite.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.turing.campsite.data.auth.DeviceAuthManager
import com.turing.campsite.data.model.User
import com.turing.campsite.data.repository.UserRepository
import com.turing.campsite.data.service.UserDeletionService
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
    private val userRepository: UserRepository = UserRepository(),
    deletionService: UserDeletionService? = null
) : BaseViewModel() {

    var uid by mutableStateOf<String?>(null)
        private set

    var deletionStatus by mutableStateOf<String?>(null)
        private set
    var accountStatusError by mutableStateOf<String?>(null)
        private set
    private var accountObservation: Job? = null
    private val deletionService by lazy { deletionService ?: UserDeletionService(users = userRepository) }

    fun observeAccountStatus() {
        val currentUid = uid ?: return
        accountObservation?.cancel()
        accountStatusError = null
        accountObservation = viewModelScope.launch {
            try {
                userRepository.observeDeletionStatus(currentUid).collect { deletionStatus = it }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (failure: Exception) { accountStatusError = failure.message ?: "Account status could not be checked" }
        }
    }

    /** Activity-scoped work survives removal of the profile/navigation screen during self-deletion. */
    fun deleteSelf() {
        val currentUid = uid ?: return
        if (isLoading) return
        launchCall {
            deletionService.delete(currentUid)
            deletionStatus = "DELETED"
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
            val removal = userRepository.deletionState(signedInUid)
            deletionStatus = removal?.status
            if (removal == null && userRepository.get(signedInUid) == null) {
                userRepository.save(User(id = signedInUid))
            }
            uid = signedInUid
        }
    }
}
