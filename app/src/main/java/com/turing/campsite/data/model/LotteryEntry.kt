package com.turing.campsite.data.model

import com.google.firebase.Timestamp

/**
 * The outcome of one entrant's lottery entry.
 *
 * PENDING until the draw, then WON or LOST. DECLINED when a winner gives up
 * their spot.
 */
enum class EntryResult { PENDING, WON, LOST, DECLINED }

/**
 * Represents one entrant on a lottery waitlist.
 *
 * Entries live in the sub-collection lotteries/{lotteryId}/entries and the
 * document id is the entrant's uid, so one entrant can only join once.
 *
 * @property id the Firestore document id, same as entrantId
 * @property entrantId the uid of the entrant who joined
 * @property joinedAt when the entrant joined the waitlist
 * @property result the outcome of this entry
 */
data class LotteryEntry(
    var id: String = "",
    var entrantId: String = "",
    var joinedAt: Timestamp = Timestamp.now(),
    var result: EntryResult = EntryResult.PENDING
)
