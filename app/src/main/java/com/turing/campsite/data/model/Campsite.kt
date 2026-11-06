package com.turing.campsite.data.model

import com.google.firebase.Timestamp

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
 * @property mapLocation the place used to show the campsite on a map
 * @property cancellationPolicy the refund terms shown to entrants
 * @property confirmationRequired true if entrants must accept the rules before reserving
 * @property active true if the campsite is listed, false if the owner has paused it
 */
data class Campsite(
    var id: String = "",
    var ownerId: String = "", // brought back because UI uses it
    var name: String = "",
    var location: String = "", // brought back because UI uses it
    var owner: String = "", // added by teammate
    var capacity: Long = 0, // added by teammate
    var description: String = "",
    var maxOccupancy: Int = 0,
    var tags: List<String> = emptyList(),
    var imageUrl: String = "",
    var ratingAverage: Double = 0.0,
    var ratingCount: Int = 0,
    var rulesText: String = "",
    var createdAt: Timestamp = Timestamp.now(),
    var mapLocation: String = "",
    var cancellationPolicy: String = "",
    var confirmationRequired: Boolean = true,
    var active: Boolean = true,
    var statusLabel: String = "Active" // added to fix the filter error
)