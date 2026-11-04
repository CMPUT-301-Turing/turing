package com.turing.campsite.data.model

import com.google.firebase.Timestamp

/**
 * Represents a rating left by an entrant for one campsite.
 *
 * Reviews live in the sub-collection campsites/{campsiteId}/reviews.
 *
 * @property id the Firestore document id
 * @property entrantId the uid of the user who wrote the review
 * @property rating the star rating from 1 to 5
 * @property comment the written feedback, optional
 * @property createdAt when the review was written
 */
data class Review(
    var id: String = "",
    var entrantId: String = "",
    var rating: Int = 0,
    var comment: String = "",
    var createdAt: Timestamp = Timestamp.now()
)
