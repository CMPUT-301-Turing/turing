package com.turing.campsite.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.turing.campsite.data.model.Campsite
import com.turing.campsite.data.model.Review
import kotlinx.coroutines.tasks.await

/**
 * Reads and writes the "campsites" collection and its "reviews" sub-collection.
 *
 * Owners use it to manage their campsites, entrants use it to browse and
 * review them, and admins use it to remove campsites.
 */
class CampsiteRepository {
    private val campsites = FirebaseFirestore.getInstance().collection("campsites")

    /**
     * Loads one campsite by id.
     *
     * @param id the Firestore document id of the campsite
     * @return the campsite, or null if there is no document with that id
     */
    suspend fun get(id: String): Campsite? {
        val doc = campsites.document(id).get().await()
        val campsite = doc.toObject(Campsite::class.java) ?: return null
        campsite.id = doc.id
        return campsite
    }

    /**
     * Loads every campsite, for the browse screen and the admin list.
     *
     * @return all campsites in the collection
     */
    suspend fun listAll(): List<Campsite> {
        val snapshot = campsites.get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Campsite::class.java)?.also { it.id = doc.id }
        }
    }

    /**
     * Loads the campsites that belong to one owner.
     *
     * @param ownerId the uid of the owner
     * @return the campsites whose ownerId matches
     */
    suspend fun listByOwner(ownerId: String): List<Campsite> {
        val snapshot = campsites.whereEqualTo("ownerId", ownerId).get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Campsite::class.java)?.also { it.id = doc.id }
        }
    }

    /**
     * Adds a new campsite with a generated id.
     *
     * @param campsite the campsite to add; its id is filled in by this call
     * @return the generated document id
     */
    suspend fun add(campsite: Campsite): String {
        val doc = campsites.document()
        campsite.id = doc.id
        doc.set(campsite).await()
        return doc.id
    }

    /**
     * Fully replaces an existing campsite document.
     *
     * @param campsite the campsite to save; its id must already be set
     */
    suspend fun update(campsite: Campsite) {
        campsites.document(campsite.id).set(campsite).await()
    }

    /**
     * Deletes a campsite document.
     *
     * @param id the Firestore document id of the campsite
     */
    suspend fun delete(id: String) {
        campsites.document(id).delete().await()
    }

    /**
     * Loads every review for one campsite.
     *
     * @param campsiteId the Firestore document id of the campsite
     * @return the reviews in the campsite's "reviews" sub-collection
     */
    suspend fun listReviews(campsiteId: String): List<Review> {
        val snapshot = campsites.document(campsiteId).collection("reviews").get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Review::class.java)?.also { it.id = doc.id }
        }
    }

    /**
     * Adds a review to a campsite and recalculates its rating.
     *
     * After the review is saved, every review for the campsite is read back
     * and the campsite's ratingAverage and ratingCount are updated.
     *
     * @param campsiteId the Firestore document id of the campsite
     * @param review the review to add; its id is filled in by this call
     */
    suspend fun addReview(campsiteId: String, review: Review) {
        val reviews = campsites.document(campsiteId).collection("reviews")
        val doc = reviews.document()
        review.id = doc.id
        doc.set(review).await()

        val all = listReviews(campsiteId)
        val average = all.map { it.rating }.average()
        campsites.document(campsiteId)
            .update("ratingAverage", average, "ratingCount", all.size)
            .await()
    }
}
