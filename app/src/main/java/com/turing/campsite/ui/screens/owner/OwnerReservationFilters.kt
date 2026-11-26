package com.turing.campsite.ui.screens.owner

import com.turing.campsite.data.model.Reservation
import com.turing.campsite.data.model.ReservationStatus
import com.turing.campsite.data.model.reservationDayStart
import com.turing.campsite.data.model.statusForOwnerHistory
import java.util.Date

/**
 * Selection logic for the owner reservation history screen.
 */
internal enum class OwnerReservationTab(
    val label: String,
    val heading: String,
    val description: String,
    private vararg val statuses: ReservationStatus
) {
    ACTIVE(
        "Active", "Upcoming stays", "Reject a booking if you can no longer host this stay.",
        ReservationStatus.ACTIVE
    ),
    PAST(
        "Past", "Completed stays", "These stays have ended. You can still message each entrant.",
        ReservationStatus.COMPLETED
    ),
    CANCELLED(
        "Cancelled", "Cancelled bookings",
        "These bookings are no longer active. Message entrants for any follow-up.",
        ReservationStatus.CANCELLED, ReservationStatus.REJECTED
    );

    fun select(
        reservations: List<Reservation>,
        dayStart: Date = reservationDayStart()
    ): List<Reservation> = reservations.filter { it.statusForOwnerHistory(dayStart) in statuses }
}

/** Keep only records belonging to the signed-in owner as a second check after the query. */
internal fun List<Reservation>.ownedBy(ownerId: String?): List<Reservation> =
    if (ownerId.isNullOrBlank()) emptyList() else filter { it.ownerId == ownerId }
