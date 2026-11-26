package com.turing.campsite.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.turing.campsite.data.model.UserRole
import com.turing.campsite.data.model.User
import kotlinx.coroutines.tasks.await

/**
 * Reads and writes the "users" collection in Firestore.
 *
 * Every screen that needs user data goes through this class instead of
 * calling Firestore directly. The document id of a user is the Firebase
 * Auth uid from DeviceAuthManager.
 */
class UserRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val users = firestore.collection("users")

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
     * Revokes the owner role without deleting the account or its historical data.
     * The transaction records the acting admin and server timestamp alongside the role change.
     * Firestore rules use this suspension for hosting writes; this action does not unlist campsites.
     * @param uid the hosting account to suspend
     * @param adminId the administrator who authorized the change
     */
    suspend fun removeOwnerAccess(uid: String, adminId: String) {
        require(uid.isNotBlank() && adminId.isNotBlank() && uid != adminId)
        val target = users.document(uid)
        val suspension = firestore.collection("ownerSuspensions").document(uid)
        firestore.runTransaction { transaction ->
            val roles = transaction.get(target).toObject(User::class.java)?.roles.orEmpty()
            check(UserRole.OWNER in roles && UserRole.ADMIN !in roles) {
                "This profile is not an eligible owner."
            }
            transaction.update(target, "roles", (roles - UserRole.OWNER).map { it.name })
            transaction.set(suspension, mapOf(
                "removedBy" to adminId,
                "removedAt" to FieldValue.serverTimestamp()
            ))
            Unit
        }.await()
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
