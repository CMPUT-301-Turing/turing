package com.turing.campsite.data.model

import com.google.firebase.Timestamp

/**
 * The states a reservation can be in.
 *
 * ACTIVE while the booking stands, COMPLETED after the stay, CANCELLED by the
 * entrant, REJECTED by the owner.
 */
enum class ReservationStatus { ACTIVE, COMPLETED, CANCELLED, REJECTED }

/**
 * Represents one document in the "reservations" collection.
 *
 * A reservation is made by a lottery winner for a campsite over a date range.
 * The ownerId is copied from the campsite so owners can query their bookings
 * in one step.
 *
 * @property id the Firestore document id
 * @property entrantId the uid of the entrant who booked
 * @property ownerId the uid of the campsite owner
 * @property campsiteId the id of the reserved campsite
 * @property lotteryId the id of the lottery the entrant won
 * @property startDate the first night of the stay
 * @property endDate the last night of the stay
 * @property status the current state of the reservation
 * @property rulesAccepted whether the entrant accepted the campsite rules
 * @property createdAt when the reservation was made
 */
data class Reservation(
    var id: String = "",
    var entrantId: String = "",
    var ownerId: String = "",
    var campsiteId: String = "",
    var lotteryId: String = "",
    var startDate: Timestamp? = null,
    var endDate: Timestamp? = null,
    var status: ReservationStatus = ReservationStatus.ACTIVE,
    var rulesAccepted: Boolean = false,
    var createdAt: Timestamp = Timestamp.now()
)
