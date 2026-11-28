/* AI Assistance Disclosure: OpenAI, ChatGPT (GPT-6), 2026-11-26.
 * Scope: Role-aware view switching using the signed-in profile and shared navigation guards.
 */
package com.turing.campsite.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.turing.campsite.data.model.User
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Reads and writes the "users" collection in Firestore.
 *
 * Every screen that needs user data goes through this class instead of
 * calling Firestore directly. The document id of a user is the Firebase
 * Auth uid from DeviceAuthManager.
 */
class UserRepository {
    private val users = FirebaseFirestore.getInstance().collection("users")

    /**
     * Loads one user by uid.
     *
     * @param uid the Firebase Auth uid of the user
     * @return the user, or null if there is no document with that id
     */
    suspend fun get(uid: String): User? {
        val doc = users.document(uid).get().await()
        val user = doc.toObject(User::class.java) ?: return null
        user.id = doc.id
        return user
    }

    /** Observe profile edits and role changes without replacing the authenticated account. */
    fun observe(uid: String): Flow<User?> = callbackFlow {
        val listener = users.document(uid).addSnapshotListener { snapshot, failure ->
            if (failure != null) {
                close(failure)
            } else {
                try {
                    val user = if (snapshot == null || !snapshot.exists()) null else {
                        // A missing roles field must not inherit User's new-account defaults.
                        val roles = snapshot.get("roles") as? List<*>
                            ?: error("Your profile has no role information")
                        val user = snapshot.toObject(User::class.java)
                            ?: error("Your profile could not be loaded")
                        user.roles = roles.map { role ->
                            check(role is String) { "Your profile has invalid role information" }
                            com.turing.campsite.data.model.UserRole.entries.firstOrNull { it.name == role }
                                ?: error("Your profile has invalid role information")
                        }
                        user.id = snapshot.id
                        user
                    }
                    trySend(user)
                } catch (failure: Exception) {
                    close(failure)
                }
            }
        }
        awaitClose { listener.remove() }
    }

    /**
     * Creates or fully replaces a user document.
     *
     * @param user the user to save; its id must already be set to the uid
     */
    suspend fun save(user: User) {
        users.document(user.id).set(user).await()
    }

    /**
     * Updates only the profile fields of a user.
     *
     * @param uid the uid of the user to update
     * @param name the new display name
     * @param email the new contact email
     * @param phone the new contact phone number
     */
    suspend fun updateProfile(uid: String, name: String, email: String, phone: String) {
        users.document(uid).update("name", name, "email", email, "phone", phone).await()
    }

    /**
     * Turns notifications on or off for a user.
     *
     * @param uid the uid of the user to update
     * @param enabled true to receive notifications, false to opt out
     */
    suspend fun setNotificationsEnabled(uid: String, enabled: Boolean) {
        users.document(uid).update("notificationsEnabled", enabled).await()
    }

    /**
     * Loads every user, for the admin screens.
     *
     * @return all users in the collection
     */
    suspend fun listAll(): List<User> {
        val snapshot = users.get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(User::class.java)?.also { it.id = doc.id }
        }
    }

    /**
     * Deletes a user document.
     *
     * @param uid the uid of the user to delete
     */
    suspend fun delete(uid: String) {
        users.document(uid).delete().await()
    }
}
