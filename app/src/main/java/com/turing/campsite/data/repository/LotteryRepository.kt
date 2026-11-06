package com.turing.campsite.data.repository

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
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
    suspend fun get(id: String): Lottery? {
        val doc = lotteries.document(id).get().await()
        val lottery = doc.toObject(Lottery::class.java) ?: return null
        lottery.id = doc.id
        return lottery
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
            doc.toObject(Lottery :: class.java)?.also{
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
        val  snapshot =lotteries
            .whereEqualTo("owner", ownerReference)
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
        val lottery = get(lotteryId) ?: return false

        if (!lottery.isRegistrationOpen())
        {
            return false
        }

        if(lottery.waitlistCap > 0 && listEntries(lotteryId).size.toLong() >= lottery.waitlistCap){
            return false
        }

        if(hasJoined(lotteryId, entrantId)) {
            return false
        }

        val entry = LotteryEntry(
            id = entrantId,
            entrantId =  entrantId
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
            doc.toObject(LotteryEntry::class.java)?.also { it.id = doc.id }
        }
    }

    /**
     * Checks whether an entrant is already on the waitlist.
     *
     * @param lotteryId the Firestore document id of the lottery
     * @param entrantId the uid of the entrant
     * @return true if the entrant has an entry document, false otherwise
     */
    suspend fun hasJoined(lotteryId: String, entrantId: String): Boolean {
        return entries(lotteryId).document(entrantId).get().await().exists()
    }

    /**
     * Records the draw result for one entrant.
     *
     * @param lotteryId the Firestore document id of the lottery
     * @param entrantId the uid of the entrant
     * @param result the new result, for example WON or LOST
     */
    suspend fun setEntryResult(lotteryId: String, entrantId: String, result: EntryResult) {
        entries(lotteryId).document(entrantId).update("result", result.name).await()
    }
}
