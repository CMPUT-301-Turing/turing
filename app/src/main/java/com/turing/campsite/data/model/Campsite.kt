package com.turing.campsite.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.google.j2objc.annotations.Property

/**
 * Represents one document in the "campsites" collection.
 *
 * A campsite is created by an owner and can be put into lotteries. Its rating
 * fields are recalculated every time a review is added.
 *
 * @property id the Firestore document id
 * @property ownerId the uid of the user who owns the campsite
 * @property name the name of the campsite
 * @property location the human-readable address
 * @property description the amenities and other details shown to entrants
 * @property maxOccupancy the maximum number of people allowed
 * @property tags keywords used for filtering, for example "lake" or "pets"
 * @property imageUrl where the campsite picture is stored, optional
 * @property ratingAverage the average of all review ratings
 * @property ratingCount how many reviews the campsite has
 * @property rulesText the rules an entrant must accept before reserving
 * @property createdAt when the campsite document was created
 */
data class Campsite(
    var id: String = "",
    var name: String = "",
    var owner: String = "",
    var capacity: Long = 0,
    var description: String = "",
    var statusLabel: String = "Active"
)
