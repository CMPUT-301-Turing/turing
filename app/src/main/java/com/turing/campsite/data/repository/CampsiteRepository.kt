/*
 * AI Assistance Disclosure
 *
 * Company: OpenAI
 * Tool: ChatGPT (GPT-6)
 * Date: 2026-11-25
 * Subject: Admin campsite deletion and associated review cleanup.
 *
 * Scope: AI-assisted implementation of deleteWithReviews() using
 * an atomic Firestore batched write. Existing methods are preserved.
 */

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
    suspend fun listByOwner(ownerId: String?): List<Campsite> {
        val snapshot = campsites.whereEqualTo("ownerId", ownerId).get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Campsite::class.java)?.also { it.id = doc.id }
        }
    }

    /**
     * Adds a new campsite with a generated id.
     *
     * @param campsite the campsite to add; its id is filled in by the repository
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
     * Deletes a campsite document without removing its reviews.
     *
     * @param id the Firestore document id of the campsite
     */
    suspend fun delete(id: String) {
        campsites.document(id).delete().await()
    }

    /**
     * Deletes a campsite and all of its reviews in a single batched write.
     *
     * A Firestore batch supports up to 500 writes. We need one write for the
     * campsite, leaving room for at most 499 review documents. If there are
     * more reviews, deletion fails without changing any documents.
     *
     * This method does not check lotteries or reservations; the deletion
     * service is responsible for enforcing those rules before calling it.
     *
     * @param campsiteId the Firestore document id of the campsite
     * @throws IllegalStateException if there are too many reviews for one batch
     */
    suspend fun deleteWithReviews(campsiteId: String) {
        val campsiteDoc = campsites.document(campsiteId)
        val reviewDocs = campsiteDoc.collection("reviews").get().await().documents

        check(reviewDocs.size <= 499) {
            "Cannot delete campsite: more than 499 reviews require bulk cleanup"
        }

        val batch = FirebaseFirestore.getInstance().batch()
        reviewDocs.forEach { reviewDoc ->
            batch.delete(reviewDoc.reference)
        }
        batch.delete(campsiteDoc)
        batch.commit().await()
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
     * @param review the review to add; its id is filled in by the repository
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