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
 * @property name is the name of the lottery provided by owner. -> integration mostly for another means of search/labeling for lottery lists
 * @property id the Firestore document id
 * @property owner the uid of the owner who created the lottery
 * @property campsiteID the ids of the campsites included in the lottery
 * @property registrationStart the first moment entrants can join
 * @property registrationEnd the last moment entrants can join
 * @property bookingStart the first date a winner can reserve
 * @property bookingEnd the last date a winner can reserve
 * @property winnerCount how many entrants will be drawn as winners
 * @property capOn toggled when owner wants a maximum waitlist size
 * @property lotteryCap the maximum waitlist size, or null for no limit
 * @property status the current stage of the lottery
 * @property multi toggled true when lottery can hold other campsite owners' campsites
 * @property ownerList is the list that has the ids of other participating owners in multi-owner lottery
 * @property waitlist is the list of entrants that want to join the lottery
 * @property createdAt when the lottery document was created
 */
data class Lottery(
    var name: String = "",
    var id: String = "",
    var owner: String = "",
    var campsiteID: List<String> = emptyList(), // this will have other owner's campsites added to it in the multi option
    var registrationStart: Timestamp? = null,
    var registrationEnd: Timestamp? = null,
    var bookingStart: Timestamp? = null,
    var bookingEnd: Timestamp? = null,
    var winnerCount: Int = 0,
    var capOn: Boolean = false, //dictates if we have a cap
    var lotteryCap: Int = 0, // what the cap is
    var status: String = "Closed", // -> will need to make a check that changes this to active with the timestamps reg start to end
    var multi: Boolean = false, // for implementation of multiple owners' campsites
    var ownerList: List<String> = emptyList(), // list of the owners with campsites in this lottery
    var waitlist: List<LotteryEntry> = emptyList(), // actual waitlist
    var createdAt: Timestamp? = null
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
