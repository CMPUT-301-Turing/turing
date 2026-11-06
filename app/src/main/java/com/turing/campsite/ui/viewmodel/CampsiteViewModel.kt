package com.turing.campsite.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.turing.campsite.data.model.Campsite
import com.turing.campsite.data.model.Review
import com.turing.campsite.data.repository.CampsiteRepository

/**
 * Holds campsite data for the owner, entrant and admin campsite screens.
 *
 * A list screen calls loadAll() or loadForOwner() and reads campsites. A
 * details screen calls select() and reads selectedCampsite and reviews.
 *
 * @property campsites the list being shown
 * @property selectedCampsite the campsite opened in a details screen, or null
 * @property reviews the reviews of the selected campsite
 */
class CampsiteViewModel(
    private val repository: CampsiteRepository = CampsiteRepository()
) : BaseViewModel() {

    var campsites by mutableStateOf<List<Campsite>>(emptyList())
        private set

    var selectedCampsite by mutableStateOf<Campsite?>(null)
        private set

    var reviews by mutableStateOf<List<Review>>(emptyList())
        private set

    /**
     * Loads every campsite, for the entrant browse screen and the admin list.
     */
    fun loadAll() {
        launchCall {
            campsites = repository.listAll()
        }
    }

    /**
     * Loads the campsites that belong to one owner, for the owner home screen.
     *
     * @param ownerId the uid of the owner
     */
    fun loadForOwner(ownerId: String) {
        launchCall {
            campsites = repository.listByOwner(ownerId)
        }
    }

    /**
     * Loads one campsite and its reviews for a details screen.
     *
     * If no campsite has that id, selectedCampsite stays null and an error
     * message is set.
     *
     * @param campsiteId the Firestore document id of the campsite
     */
    fun select(campsiteId: String) {
        launchCall {
            val campsite = repository.get(campsiteId)
            selectedCampsite = campsite
            if (campsite == null) {
                reviews = emptyList()
                errorMessage = "Campsite not found"
            } else {
                reviews = repository.listReviews(campsiteId)
            }
        }
    }

    /**
     * Saves a new campsite and adds it to campsites.
     *
     * @param campsite the campsite to add; its id is filled in by the repository
     */
    fun add(campsite: Campsite) {
        launchCall {
            repository.add(campsite)
            campsites = campsites + campsite
        }
    }

    /**
     * Saves changes to a campsite and replaces the old copy in campsites.
     *
     * @param campsite the edited campsite; its id must already be set
     */
    fun update(campsite: Campsite) {
        launchCall {
            repository.update(campsite)
            campsites = campsites.map { if (it.id == campsite.id) campsite else it }
            if (selectedCampsite?.id == campsite.id) selectedCampsite = campsite
        }
    }

    /**
     * Deletes a campsite and removes it from campsites.
     *
     * @param campsiteId the Firestore document id of the campsite
     */
    fun delete(campsiteId: String) {
        launchCall {
            repository.delete(campsiteId)
            campsites = campsites.filter { it.id != campsiteId }
            if (selectedCampsite?.id == campsiteId) selectedCampsite = null
        }
    }

    /**
     * Adds a review, then reloads the campsite so the new rating shows.
     *
     * @param campsiteId the Firestore document id of the campsite
     * @param review the review to add
     */
    fun addReview(campsiteId: String, review: Review) {
        launchCall {
            repository.addReview(campsiteId, review)
            selectedCampsite = repository.get(campsiteId)
            reviews = repository.listReviews(campsiteId)
        }
    }
}
