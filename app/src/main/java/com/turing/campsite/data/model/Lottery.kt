package com.turing.campsite.data.model

import com.google.firebase.Timestamp

/**
 * The stages a lottery goes through.
 *
 * OPEN while entrants can join, DRAWN once winners have been picked,
 * CLOSED when the owner ends it.
 */
enum class LotteryStatus { OPEN, DRAWN, CLOSED }

/**
 * Represents one document in the "lotteries" collection.
 *
 * An owner runs a lottery for one or more campsites. Entrants join the
 * waitlist during the registration period, then winners are drawn and can
 * reserve dates inside the booking period.
 *
 * @property id the Firestore document id
 * @property ownerId the uid of the owner who created the lottery
 * @property campsiteIds the ids of the campsites included in the lottery
 * @property registrationStart the first moment entrants can join
 * @property registrationEnd the last moment entrants can join
 * @property bookingStart the first date a winner can reserve
 * @property bookingEnd the last date a winner can reserve
 * @property winnerCount how many entrants will be drawn as winners
 * @property waitlistCapacity the maximum waitlist size, or null for no limit
 * @property status the current stage of the lottery
 * @property createdAt when the lottery document was created
 */
data class Lottery(
    var id: String = "",
    var owner: String = "",
    var campsiteID: String = "",
    var registrationStart: Timestamp? = null,
    var registrationEnd: Timestamp? = null,
    var bookingStart: Timestamp? = null,
    var bookingEnd: Timestamp? = null,
    var winnerCount: Int = 0,
    var waitlistCap: Long = 0,
    var status: String = "closed"
) {
    /**
     * Checks whether entrants can still join the lottery at a given time.
     *
     * Registration is open only when the status is OPEN and the time is
     * between registrationStart and registrationEnd, both inclusive. If either
     * date is missing the lottery is treated as closed.
     *
     * @param now the time to check against
     * @return true if an entrant could join at that time, false otherwise
     */
    fun isRegistrationOpen(now: Timestamp = Timestamp.now()): Boolean {
        val start = registrationStart ?: return false
        val end = registrationEnd ?: return false
        return status.equals("open", ignoreCase = true) && now >=start && now <= end
    }
}
