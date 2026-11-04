package com.turing.campsite.data.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

/**
 * Handles device-based identity with Firebase Anonymous Auth.
 *
 * The phone is signed in anonymously on first launch, so the app never asks
 * for a username or password. The uid Firebase returns is used as the id of
 * the user's document in the "users" collection.
 *
 * @property currentUid the uid of the signed-in phone, or null before signIn() has run
 */
class DeviceAuthManager {
    private val auth = FirebaseAuth.getInstance()

    val currentUid: String?
        get() = auth.currentUser?.uid

    /**
     * Signs the phone in anonymously, or reuses the existing sign-in.
     *
     * Calling this more than once is safe: if the phone is already signed in
     * the same uid is returned without talking to Firebase again.
     *
     * @return the uid of the signed-in phone
     */
    suspend fun signIn(): String {
        val existing = auth.currentUser
        if (existing != null) return existing.uid
        val result = auth.signInAnonymously().await()
        return result.user!!.uid
    }

    /**
     * Signs the phone out so the next signIn() creates a brand new uid.
     */
    fun signOut() {
        auth.signOut()
    }
}
