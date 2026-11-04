package com.turing.campsite.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.turing.campsite.data.model.Reservation
import com.turing.campsite.data.model.ReservationStatus
import kotlinx.coroutines.tasks.await

/**
 * Reads and writes the "reservations" collection in Firestore.
 *
 * Entrants use it to make and cancel bookings, owners use it to see and
 * reject bookings on their campsites.
 */
class ReservationRepository {
    private val reservations = FirebaseFirestore.getInstance().collection("reservations")

    /**
     * Loads one reservation by id.
     *
     * @param id the Firestore document id of the reservation
     * @return the reservation, or null if there is no document with that id
     */
    suspend fun get(id: String): Reservation? {
        val doc = reservations.document(id).get().await()
        val reservation = doc.toObject(Reservation::class.java) ?: return null
        reservation.id = doc.id
        return reservation
    }

    /**
     * Loads the reservations made by one entrant.
     *
     * @param entrantId the uid of the entrant
     * @return the reservations whose entrantId matches
     */
    suspend fun listByEntrant(entrantId: String): List<Reservation> {
        val snapshot = reservations.whereEqualTo("entrantId", entrantId).get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Reservation::class.java)?.also { it.id = doc.id }
        }
    }

    /**
     * Loads the reservations on campsites belonging to one owner.
     *
     * @param ownerId the uid of the owner
     * @return the reservations whose ownerId matches
     */
    suspend fun listByOwner(ownerId: String): List<Reservation> {
        val snapshot = reservations.whereEqualTo("ownerId", ownerId).get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Reservation::class.java)?.also { it.id = doc.id }
        }
    }

    /**
     * Loads the active reservations for one campsite.
     *
     * This is how the owner sees which dates are already taken. Cancelled,
     * rejected and completed reservations are left out.
     *
     * @param campsiteId the Firestore document id of the campsite
     * @return the reservations for that campsite whose status is ACTIVE
     */
    suspend fun listActiveForCampsite(campsiteId: String): List<Reservation> {
        val snapshot = reservations
            .whereEqualTo("campsiteId", campsiteId)
            .whereEqualTo("status", ReservationStatus.ACTIVE.name)
            .get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Reservation::class.java)?.also { it.id = doc.id }
        }
    }

    /**
     * Adds a new reservation with a generated id.
     *
     * @param reservation the reservation to add; its id is filled in by this call
     * @return the generated document id
     */
    suspend fun add(reservation: Reservation): String {
        val doc = reservations.document()
        reservation.id = doc.id
        doc.set(reservation).await()
        return doc.id
    }

    /**
     * Changes the status of a reservation.
     *
     * Used for cancel by the entrant (CANCELLED) and reject by the owner
     * (REJECTED).
     *
     * @param id the Firestore document id of the reservation
     * @param status the new status
     */
    suspend fun setStatus(id: String, status: ReservationStatus) {
        reservations.document(id).update("status", status.name).await()
    }
}
