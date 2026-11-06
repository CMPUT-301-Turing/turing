package com.turing.campsite.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.firestore.DocumentReference
import com.turing.campsite.data.model.Lottery
import com.turing.campsite.data.model.LotteryEntry
import com.turing.campsite.data.model.LotteryStatus
import com.turing.campsite.data.repository.LotteryRepository

/**
 * Holds lottery data for the owner lottery screens and the entrant waitlist.
 *
 * A list screen calls loadOpen() or loadForOwner() and reads lotteries. A
 * details screen calls select() and reads selectedLottery and entries.
 *
 * @property lotteries the list being shown
 * @property selectedLottery the lottery opened in a details screen, or null
 * @property entries the waitlist of the selected lottery
 * @property hasJoined whether the entrant passed to select() is on the waitlist
 */
class LotteryViewModel(
    private val repository: LotteryRepository = LotteryRepository()
) : BaseViewModel() {

    var lotteries by mutableStateOf<List<Lottery>>(emptyList())
        private set

    var selectedLottery by mutableStateOf<Lottery?>(null)
        private set

    var entries by mutableStateOf<List<LotteryEntry>>(emptyList())
        private set

    var hasJoined by mutableStateOf(false)
        private set

    /**
     * Loads the lotteries entrants can currently join.
     */
    fun loadOpen() {
        launchCall {
            lotteries = repository.listOpen()
        }
    }

    /**
     * Loads the lotteries created by one owner.
     *
     * @param ownerId the uid of the owner
     */
    fun loadForOwner(ownerReference: DocumentReference) {
        launchCall {
            lotteries = repository.listByOwner(ownerReference)
        }
    }

    /**
     * Loads one lottery and its waitlist for a details screen.
     *
     * Also checks whether the given entrant is already on the waitlist, so
     * the screen can show Join or Leave.
     *
     * @param lotteryId the Firestore document id of the lottery
     * @param entrantId the uid of the user looking at the lottery
     */
    fun select(lotteryId: String, entrantId: String) {
        launchCall {
            val lottery = repository.get(lotteryId)
            selectedLottery = lottery
            if (lottery == null) {
                entries = emptyList()
                hasJoined = false
                errorMessage = "Lottery not found"
            } else {
                entries = repository.listEntries(lotteryId)
                hasJoined = entries.any { it.entrantId == entrantId }
            }
        }
    }

    /**
     * Saves a new lottery and adds it to lotteries.
     *
     * @param lottery the lottery to add; its id is filled in by the repository
     */
    fun add(lottery: Lottery) {
        launchCall {
            repository.add(lottery)
            lotteries = lotteries + lottery
        }
    }

    /**
     * Saves changes to a lottery and replaces the old copy in lotteries.
     *
     * @param lottery the edited lottery; its id must already be set
     */
    fun update(lottery: Lottery) {
        launchCall {
            repository.update(lottery)
            lotteries = lotteries.map { if (it.id == lottery.id) lottery else it }
            if (selectedLottery?.id == lottery.id) selectedLottery = lottery
        }
    }

    /**
     * Moves a lottery to a new stage, for example OPEN to CLOSED.
     *
     * @param lotteryId the Firestore document id of the lottery
     * @param status the new status
     */
    fun setStatus(lotteryId: String, status: LotteryStatus) {
        launchCall {
            val newStatus = status.name.lowercase()

            repository.setStatus(lotteryId, newStatus)

            lotteries = lotteries.map {
                if (it.id == lotteryId) it.copy(status = newStatus) else it
            }
            if (selectedLottery?.id == lotteryId) {
                selectedLottery = selectedLottery?.copy(status = newStatus)
            }
        }
    }

    /**
     * Puts an entrant on the waitlist of a lottery.
     *
     * If the waitlist is full nothing changes and an error message is set.
     *
     * @param lotteryId the Firestore document id of the lottery
     * @param entrantId the uid of the entrant joining
     */
    fun join(lotteryId: String, entrantId: String) {
        launchCall {
            val joined = repository.join(lotteryId, entrantId)
            if (joined) {
                hasJoined = true
                entries = repository.listEntries(lotteryId)
            } else {
                errorMessage = "Could not join, the waitlist is full"
            }
        }
    }

    /**
     * Removes an entrant from the waitlist of a lottery.
     *
     * @param lotteryId the Firestore document id of the lottery
     * @param entrantId the uid of the entrant leaving
     */
    fun leave(lotteryId: String, entrantId: String) {
        launchCall {
            repository.leave(lotteryId, entrantId)
            hasJoined = false
            entries = entries.filter { it.entrantId != entrantId }
        }
    }

    /**
     * Loads the lottery a campsite is listed in.
     *
     * @param campsiteId the Firestore document id of the campsite
     */
    fun loadLotteryForCampsite(campsiteId: String) {
        // TODO: not written yet, nothing calls this so far
    }
}
