package com.turing.campsite.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.turing.campsite.data.model.Reservation
import com.turing.campsite.data.model.ReservationStatus
import com.turing.campsite.data.repository.ReservationRepository

/**
 * Holds reservation data for the entrant and owner reservation screens.
 *
 * Screens call one of the load functions and read reservations.
 *
 * @property reservations the list being shown
 */
class ReservationViewModel(
    private val repository: ReservationRepository = ReservationRepository()
) : BaseViewModel() {

    var reservations by mutableStateOf<List<Reservation>>(emptyList())
        private set

    /**
     * Loads the reservations made by one entrant.
     *
     * @param entrantId the uid of the entrant
     */
    fun loadForEntrant(entrantId: String) {
        launchCall {
            reservations = repository.listByEntrant(entrantId)
        }
    }

    /**
     * Loads the reservations on campsites belonging to one owner.
     *
     * @param ownerId the uid of the owner
     */
    fun loadForOwner(ownerId: String) {
        launchCall {
            reservations = repository.listByOwner(ownerId)
        }
    }

    /**
     * Loads the active reservations for one campsite, to show taken dates.
     *
     * @param campsiteId the Firestore document id of the campsite
     */
    fun loadActiveForCampsite(campsiteId: String) {
        launchCall {
            reservations = repository.listActiveForCampsite(campsiteId)
        }
    }

    /**
     * Saves a new reservation and adds it to reservations.
     *
     * @param reservation the reservation to add; its id is filled in by the repository
     */
    fun add(reservation: Reservation) {
        launchCall {
            repository.add(reservation)
            reservations = reservations + reservation
        }
    }

    /**
     * Cancels a reservation, used by the entrant who made it.
     *
     * @param reservationId the Firestore document id of the reservation
     */
    fun cancel(reservationId: String) {
        setStatus(reservationId, ReservationStatus.CANCELLED)
    }

    /**
     * Rejects a reservation, used by the owner of the campsite.
     *
     * @param reservationId the Firestore document id of the reservation
     */
    fun reject(reservationId: String) {
        setStatus(reservationId, ReservationStatus.REJECTED)
    }

    private fun setStatus(reservationId: String, status: ReservationStatus) {
        launchCall {
            repository.setStatus(reservationId, status)
            reservations = reservations.map {
                if (it.id == reservationId) it.copy(status = status) else it
            }
        }
    }
}
