/* AI Assistance Disclosure: OpenAI, ChatGPT (GPT-6), 2026-11-26.
 * Scope: Scalar/array lottery discovery, participant capture and transactional campsite removal.
 */

/*
 * AI Assistance Disclosure
 *
 * Company: OpenAI
 * Tool: ChatGPT (GPT-6)
 * Date: 2026-11-25
 * Subject: Lottery repository queries for campsite deletion
 *          dependency checking.
 *
 * Scope: AI-assisted implementation of listByCampsite()
 * and review of the repository.
 */

package com.turing.campsite.data.repository

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.turing.campsite.data.model.EntryResult
import com.turing.campsite.data.model.Lottery
import com.turing.campsite.data.model.LotteryEntry
import com.turing.campsite.data.model.LotteryStatus
import kotlinx.coroutines.tasks.await

/**
 * Reads and writes the "lotteries" collection and its "entries" sub-collection.
 *
 * Owners use it to create and draw lotteries, entrants use it to join and
 * leave waitlists. Each entry document is keyed by the entrant's uid so an
 * entrant can only be on a waitlist once.
 */
/** Deliberately independent of Lottery's scalar/list schema during lottery-system integration. */
data class CampsiteLotteryLink(val id: String, val campsiteIds: List<String>)

enum class CampsiteLotteryRemoval { RETAINED, DELETE_LOTTERY, ALREADY_REMOVED }

class LotteryRepository {
    private val lotteries = FirebaseFirestore.getInstance().collection("lotteries")

    private fun entries(lotteryId: String) =
        lotteries.document(lotteryId).collection("entries")

    /**
     * Loads one lottery by id.
     *
     * @param id the Firestore document id of the lottery
     * @return the lottery, or null if there is no document with that id
     */
    suspend fun getLottery(id: String): Lottery? {
        val doc = lotteries.document(id).get().await()
        val lottery = doc.toObject(Lottery::class.java) ?: return null
        lottery.id = doc.id
        return lottery
    }

    /**
     * Finds a lottery ID using a given campsiteID
     * @param campsiteId the campsiteID field value
     * @return the lotteryID with this campsiteID
     */
    suspend fun findLot(campsiteId: String): Lottery? {
        val db = Firebase.firestore
        val snapshot = db.collection("lotteries")
            .whereEqualTo("campsiteID", campsiteId)
            .get()
            .await()

        val document = snapshot.documents.firstOrNull()
        val lottery = getLottery(document!!.id)
        return lottery
    }

    /**
     * Loads all lotteries associated with a campsite.
     *
     * Includes lotteries regardless of their status. The caller is
     * responsible for coordinating lifecycle cleanup.
     *
     * @param campsiteId the Firestore ID of the campsite
     * @return all matching lotteries, or an empty list if none exist
     */
    suspend fun listByCampsite(campsiteId: String): List<Lottery> {
        val snapshot = lotteries
            .whereEqualTo("campsiteID", campsiteId)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Lottery::class.java)?.also {
                it.id = doc.id
            }
        }
    }

    /** Supports today's scalar link and lottery_system's array without deserializing Lottery. */
    suspend fun listByCampsiteForDeletion(campsiteId: String): List<CampsiteLotteryLink> {
        val scalar = lotteries.whereEqualTo("campsiteID", campsiteId)
            .get(com.google.firebase.firestore.Source.SERVER).await()
        val multiple = lotteries.whereArrayContains("campsiteID", campsiteId)
            .get(com.google.firebase.firestore.Source.SERVER).await()
        return (scalar.documents + multiple.documents).distinctBy { it.id }.map { doc ->
            CampsiteLotteryLink(doc.id, doc.campsiteIds())
        }
    }

    /** Freeze participation before taking the notification recipient snapshot. */
    suspend fun beginCampsiteRemoval(lotteryId: String, campsiteId: String) {
        val ref = lotteries.document(lotteryId)
        FirebaseFirestore.getInstance().runTransaction { tx ->
            val snapshot = tx.get(ref)
            check(snapshot.exists() && campsiteId in snapshot.campsiteIds()) {
                "Lottery changed before participant capture; retry campsite deletion"
            }
            tx.update(ref, "removingCampsiteIds",
                com.google.firebase.firestore.FieldValue.arrayUnion(campsiteId))
        }.await()
    }

    /** Includes entries and the embedded participant lists present on lottery_system. */
    suspend fun listParticipantIdsForRemoval(lotteryId: String): List<String> {
        val doc = lotteries.document(lotteryId)
            .get(com.google.firebase.firestore.Source.SERVER).await()
        check(doc.exists()) { "Lottery disappeared before participant capture" }
        val embedded = listOf("lotteryList", "waitlist").flatMap { field ->
            val raw = doc.get(field) ?: return@flatMap emptyList<String>()
            check(raw is List<*>) { "Invalid lottery $field" }
            raw.map { value ->
                val entrantId = (value as? Map<*, *>)?.get("entrantId")
                check(entrantId is String && entrantId.isNotBlank()) { "Invalid participant in $field" }
                entrantId
            }
        }
        return (listEntriesForDeletion(lotteryId).map { it.entrantId } + embedded).distinct()
    }

    /** Transactions prevent concurrent removals from overwriting each other's campsite lists. */
    suspend fun removeCampsite(lotteryId: String, campsiteId: String): CampsiteLotteryRemoval {
        val ref = lotteries.document(lotteryId)
        return FirebaseFirestore.getInstance().runTransaction { tx ->
            val snapshot = tx.get(ref)
            if (!snapshot.exists() || campsiteId !in snapshot.campsiteIds()) {
                CampsiteLotteryRemoval.ALREADY_REMOVED
            } else {
                val locked = snapshot.get("removingCampsiteIds") as? List<*> ?: emptyList<Any>()
                check(campsiteId in locked) { "Lottery removal gate is missing" }
                val remaining = snapshot.campsiteIds().filterNot { it == campsiteId }
                if (remaining.isEmpty()) {
                    // Keep the last link and gate until entries are explicitly deleted.
                    CampsiteLotteryRemoval.DELETE_LOTTERY
                } else {
                    tx.update(ref, mapOf(
                        "campsiteID" to remaining,
                        "removingCampsiteIds" to locked.filterNot { it == campsiteId }
                    ))
                    CampsiteLotteryRemoval.RETAINED
                }
            }
        }.await()
    }

    /** Recheck the last link before finalizing removal of a now-empty lottery. */
    suspend fun deleteForCampsiteRemoval(lotteryId: String, campsiteId: String) {
        val ref = lotteries.document(lotteryId)
        FirebaseFirestore.getInstance().runTransaction { tx ->
            val snapshot = tx.get(ref)
            if (snapshot.exists()) {
                check(snapshot.campsiteIds() == listOf(campsiteId)) { "Lottery still contains other campsites" }
                check(campsiteId in (snapshot.get("removingCampsiteIds") as? List<*> ?: emptyList<Any>())) {
                    "Lottery removal gate is missing"
                }
                tx.delete(ref)
            }
        }.await()
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.campsiteIds(): List<String> {
        val value = get("campsiteID")
        val ids = when (value) {
            is String -> listOf(value)
            is List<*> -> value.map {
                check(it is String && it.isNotBlank()) { "Invalid lottery campsite ID" }
                it
            }
            else -> error("Invalid lottery campsite links")
        }
        check(ids.isNotEmpty() && ids.all { it.isNotBlank() } && ids.distinct().size == ids.size) {
            "Lottery campsite links are empty or duplicated"
        }
        return ids
    }

    suspend fun listEntriesForDeletion(lotteryId: String): List<LotteryEntry> {
        return entries(lotteryId).get(com.google.firebase.firestore.Source.SERVER).await().documents.map { doc ->
            requireNotNull(doc.toObject(LotteryEntry::class.java)) { "Invalid entry ${doc.id}" }
                .also {
                    it.id = doc.id
                    check(it.entrantId.isNotBlank()) { "Entry ${doc.id} has no entrant" }
                }
        }
    }

    /**
     * Loads the lotteries entrants can currently see and join.
     *
     * @return the lotteries whose status is OPEN
     */
    suspend fun listOpen(): List<Lottery> {
        val snapshot = lotteries
            .whereEqualTo("status", "open")
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Lottery::class.java)?.also {
                it.id = doc.id
            }
        }
    }

    /**
     * Loads the lotteries created by one owner.
     *
     * @param ownerId the uid of the owner
     * @return the lotteries whose ownerId matches
     */
    suspend fun listByOwner(ownerReference: DocumentReference): List<Lottery> {
        val snapshot = lotteries
            .whereEqualTo("ownerId", ownerReference.id)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Lottery::class.java)?.also {
                it.id = doc.id
            }
        }
    }

    /**
     * Adds a new lottery with a generated id.
     *
     * @param lottery the lottery to add; its id is filled in by this call
     * @return the generated document id
     */
    suspend fun add(lottery: Lottery): String {
        val doc = lotteries.document()
        lottery.id = doc.id
        doc.set(lottery).await()
        return doc.id
    }

    /**
     * Fully replaces an existing lottery document.
     *
     * @param lottery the lottery to save; its id must already be set
     */
    suspend fun update(lottery: Lottery) {
        lotteries.document(lottery.id).set(lottery).await()
    }

    /**
     * Moves a lottery to a new stage, for example OPEN to DRAWN.
     *
     * @param lotteryId the Firestore document id of the lottery
     * @param status the new status
     */
    suspend fun setStatus(lotteryId: String, status: String) {
        lotteries.document(lotteryId)
            .update("status", status.lowercase())
            .await()
    }

    /**
     * Puts an entrant on the lottery waitlist.
     *
     * If the lottery has a waitlistCapacity and the waitlist is already full,
     * nothing is written and false is returned.
     *
     * @param lotteryId the Firestore document id of the lottery
     * @param entrantId the uid of the entrant joining
     * @return true if the entrant was added, false if the lottery was missing or full
     */
    suspend fun join(lotteryId: String, entrantId: String): Boolean {
        val lottery = getLottery(lotteryId) ?: return false

        if (!lottery.isRegistrationOpen()) {
            return false
        }

        if (lottery.waitlistCap > 0 &&
            listEntries(lotteryId).size.toLong() >= lottery.waitlistCap
        ) {
            return false
        }

        if (hasJoined(lotteryId, entrantId)) {
            return false
        }

        val entry = LotteryEntry(
            id = entrantId,
            entrantId = entrantId
        )

        entries(lotteryId)
            .document(entrantId)
            .set(entry)
            .await()

        return true
    }

    /**
     * Removes an entrant from the lottery waitlist.
     *
     * @param lotteryId the Firestore document id of the lottery
     * @param entrantId the uid of the entrant leaving
     */
    suspend fun leave(lotteryId: String, entrantId: String) {
        entries(lotteryId).document(entrantId).delete().await()
    }

    /**
     * Loads the whole waitlist for one lottery.
     *
     * @param lotteryId the Firestore document id of the lottery
     * @return every entry in the lottery's "entries" sub-collection
     */
    suspend fun listEntries(lotteryId: String): List<LotteryEntry> {
        val snapshot = entries(lotteryId).get().await()

        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(LotteryEntry::class.java)?.also {
                it.id = doc.id
            }
        }
    }

    /** Deletes entries explicitly; deleting a lottery never deletes its subcollections. */
    suspend fun deleteEntries(lotteryId: String) {
        while (true) {
            val documents = entries(lotteryId).limit(400)
                .get(com.google.firebase.firestore.Source.SERVER).await().documents
            if (documents.isEmpty()) return
            val batch = FirebaseFirestore.getInstance().batch()
            documents.forEach { batch.delete(it.reference) }
            batch.commit().await()
        }
    }

    suspend fun delete(lotteryId: String) {
        lotteries.document(lotteryId).delete().await()
    }

    /**
     * Checks whether an entrant is already on the waitlist.
     *
     * @param lotteryId the Firestore document id of the lottery
     * @param entrantId the uid of the entrant
     * @return true if the entrant has an entry document
     */
    suspend fun hasJoined(lotteryId: String, entrantId: String): Boolean {
        return entries(lotteryId)
            .document(entrantId)
            .get()
            .await()
            .exists()
    }

    /**
     * Records the draw result for one entrant.
     *
     * @param lotteryId the Firestore document id of the lottery
     * @param entrantId the uid of the entrant
     * @param result the new result, for example WON or LOST
     */
    suspend fun setEntryResult(
        lotteryId: String,
        entrantId: String,
        result: EntryResult
    ) {
        entries(lotteryId)
            .document(entrantId)
            .update("result", result.name)
            .await()
    }
}
