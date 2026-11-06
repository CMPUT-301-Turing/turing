package com.turing.campsite.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

/**
 * Shared base for the view models in the app.
 *
 * Holds the loading flag and error message every screen needs, and runs
 * repository calls in the background so a screen never has to call a suspend
 * function itself.
 *
 * @property isLoading true while a repository call is running
 * @property errorMessage what went wrong in the last call, or null if it worked
 */
abstract class BaseViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
        protected set

    var errorMessage by mutableStateOf<String?>(null)
        protected set

    /**
     * Runs a repository call in the background.
     *
     * Sets isLoading while the call runs. If the call throws, the message is
     * stored in errorMessage instead of crashing the app.
     *
     * @param block the repository call to run
     */
    protected fun launchCall(block: suspend () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                errorMessage = e.message ?: "Something went wrong"
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Clears the error message after the screen has shown it.
     */
    fun clearError() {
        errorMessage = null
    }
}
