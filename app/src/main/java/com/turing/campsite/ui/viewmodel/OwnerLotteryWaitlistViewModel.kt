package com.turing.campsite.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.turing.campsite.data.model.Campsite
import com.turing.campsite.data.model.Lottery
import com.turing.campsite.data.model.LotteryEntry
import com.turing.campsite.data.repository.CampsiteRepository
import com.turing.campsite.data.repository.LotteryRepository

/**
 * Checks the selected campsite owner, resolves its lottery, and loads its entries.
 */
internal class OwnerLotteryWaitlistViewModel : BaseViewModel() {
    private val lotteryRepository by lazy { LotteryRepository() }
    private val campsiteRepository by lazy { CampsiteRepository() }

    var campsite by mutableStateOf<Campsite?>(null)
        private set

    var lottery by mutableStateOf<Lottery?>(null)
        private set
    var entries by mutableStateOf<List<LotteryEntry>>(emptyList())
        private set
    var statusMessage by mutableStateOf<String?>(null)
        private set

    /**
     * Loads entries from the linked lottery document after confirming campsite ownership.
     *
     * The existing lookup selects one lottery. If a campsite can have multiple lotteries,
     * the caller will need to identify which lottery to display.
     * @param ownerId the authenticated owner's UID
     * @param campsiteId the ID selected in the owner campsite list
     */
    fun load(ownerId: String, campsiteId: String) {
        campsite = null
        lottery = null
        entries = emptyList()
        statusMessage = null
        if (campsiteId.isBlank()) {
            statusMessage = "No campsite selected."
            return
        }
        launchCall { loadDetails(ownerId, campsiteId) }
    }

    private suspend fun loadDetails(ownerId: String, campsiteId: String) {
        statusMessage = null
        val selectedCampsite = campsiteRepository.get(campsiteId)
        if (selectedCampsite?.ownerId != ownerId) {
            statusMessage = "Campsite not found for this owner."
            return
        }
        campsite = selectedCampsite
        val selected = lotteryRepository.findLot(campsiteId)
        if (selected == null || selected.id.isBlank()) {
            statusMessage = "No lottery has been created for this campsite."
            return
        }
        entries = lotteryRepository.listEntries(selected.id)
        lottery = selected
    }
}
