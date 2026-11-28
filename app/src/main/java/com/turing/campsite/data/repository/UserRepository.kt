/* AI Assistance Disclosure: OpenAI, ChatGPT (GPT-6), 2026-11-27.
 * Scope: Shared self/admin profile deletion, deleted-UID recovery and related record cleanup.
 */
package com.turing.campsite.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.Source
import com.turing.campsite.data.model.User
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@IgnoreExtraProperties
data class UserDeletionPlan(
    val campsiteIds: List<String> = emptyList(),
    val lotteryIds: List<String> = emptyList(),
    val reservationIds: List<String> = emptyList(),
    val reviewCampsiteIds: List<String> = emptyList(),
    val ownerRecipientIds: List<String> = emptyList(),
    val entrantRecipientIds: List<String> = emptyList()
) {
    val recipientIds: List<String> get() = (ownerRecipientIds + entrantRecipientIds).distinct()
}

data class UserDeletionState(
    val status: String,
    val wasAdmin: Boolean,
    val plan: UserDeletionPlan?
)

/**
 * Reads and writes the "users" collection in Firestore.
 *
 * Every screen that needs user data goes through this class instead of
 * calling Firestore directly. The document id of a user is the Firebase
 * Auth uid from DeviceAuthManager.
 */
class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val users = db.collection("users")
    private val deletedUsers by lazy { db.collection("deletedUsers") }

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

    /** Deletion decisions use a server read so cached roles cannot authorize removal. */
    suspend fun getForDeletion(uid: String): User? {
        val doc = users.document(uid).get(Source.SERVER).await()
        if (!doc.exists()) return null
        check(doc.get("roles") is List<*>) { "User role information is missing" }
        return requireNotNull(doc.toObject(User::class.java)).also { it.id = uid }
    }

    suspend fun deletionState(uid: String): UserDeletionState? {
        val doc = deletedUsers.document(uid).get(Source.SERVER).await()
        return if (doc.exists()) doc.toDeletionState() else null
    }

    /** Keeps the app's removal/recovery screen in sync with the registry. */
    fun observeDeletionStatus(uid: String): Flow<String?> = callbackFlow {
        val listener = deletedUsers.document(uid).addSnapshotListener { doc, error ->
            if (error != null) close(error)
            else trySend(doc?.getString("status"))
        }
        awaitClose { listener.remove() }
    }

    /** Create the gate once; retries return the saved state. */
    suspend fun beginDeletion(uid: String, wasAdmin: Boolean): UserDeletionState {
        val ref = deletedUsers.document(uid)
        return db.runTransaction { tx ->
            val doc = tx.get(ref)
            if (doc.exists()) {
                return@runTransaction doc.toDeletionState()
            }
            tx.set(ref, mapOf(
                "status" to "DELETING",
                "wasAdmin" to wasAdmin,
                "planCaptured" to false,
                "notifiedRecipientIds" to emptyList<String>()
            ))
            UserDeletionState("DELETING", wasAdmin, null)
        }.await()
    }

    /** Save the first plan before cleanup starts; never overwrite it on retry. */
    suspend fun captureDeletionPlan(uid: String, plan: UserDeletionPlan): UserDeletionPlan {
        val ref = deletedUsers.document(uid)
        return db.runTransaction { tx ->
            val state = tx.get(ref).toDeletionState()
            check(state.status == "DELETING") { "User deletion is not pending" }
            state.plan?.let { return@runTransaction it }

            tx.update(ref, mapOf(
                "planCaptured" to true,
                "campsiteIds" to plan.campsiteIds,
                "lotteryIds" to plan.lotteryIds,
                "reservationIds" to plan.reservationIds,
                "reviewCampsiteIds" to plan.reviewCampsiteIds,
                "ownerRecipientIds" to plan.ownerRecipientIds,
                "entrantRecipientIds" to plan.entrantRecipientIds,
                "recipientIds" to plan.recipientIds
            ))
            plan
        }.await()
    }

    suspend fun successor(uid: String, lotteryId: String): String? {
        return deletedUsers.document(uid).collection("successors").document(lotteryId)
            .get(Source.SERVER).await().getString("ownerId")
    }

    /** The service selects/reuses the owner; Firestore rules validate this write. */
    suspend fun saveSuccessor(uid: String, lotteryId: String, candidate: String): String {
        deletedUsers.document(uid).collection("successors").document(lotteryId)
            .set(mapOf("ownerId" to candidate)).await()
        return candidate
    }

    suspend fun finishDeletion(uid: String) {
        val ref = deletedUsers.document(uid)
        val decisions = ref.collection("successors").get(Source.SERVER).await()
        for (group in decisions.documents.chunked(400)) {
            val batch = db.batch()
            group.forEach { batch.delete(it.reference) }
            batch.commit().await()
        }

        // Remove the profile and keep the permanent UID block in the same transaction.
        db.runTransaction { tx ->
            val doc = tx.get(ref)
            val state = doc.toDeletionState()
            if (state.status == "DELETED") return@runTransaction

            val recipients = requireNotNull(state.plan) { "Deletion plan is missing" }.recipientIds
            val delivered = doc.get("notifiedRecipientIds") as? List<*> ?: emptyList<Any>()
            check(delivered.containsAll(recipients)) { "User removal notifications are incomplete" }

            tx.delete(users.document(uid))
            tx.set(ref, mapOf(
                "status" to "DELETED",
                "wasAdmin" to state.wasAdmin,
                "deletedAt" to Timestamp.now()
            ))
        }.await()
    }

    private fun DocumentSnapshot.toDeletionState(): UserDeletionState {
        val status = getString("status")
        check(status == "DELETING" || status == "DELETED") { "Invalid deletion status" }
        val plan = if (getBoolean("planCaptured") == true) {
            toObject(UserDeletionPlan::class.java)
        } else null
        return UserDeletionState(requireNotNull(status), getBoolean("wasAdmin") == true, plan)
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
