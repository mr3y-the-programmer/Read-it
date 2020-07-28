/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.publisher

import com.google.firebase.firestore.*
import com.secret.readit.core.data.utils.withIds
import com.secret.readit.core.data.utils.wrapInCoroutineCancellable
import com.secret.readit.core.di.IoDispatcher
import com.secret.readit.core.result.Result
import com.secret.readit.model.Publisher
import com.secret.readit.model.articleId
import com.secret.readit.model.publisherId
import kotlinx.coroutines.CoroutineDispatcher
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Our publisherInfoDataSource has one responsibility, interact directly with firestore to get/set data
 */
internal class DefaultPublisherInfoDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : PublisherInfoDataSource {

    override suspend fun getPublisherId(publisher: PubImportantInfo): Result<publisherId> {
        return wrapInCoroutineCancellable(ioDispatcher) { continuation ->
            firestore.collection(PUBLISHERS_COLLECTION)
                .whereEqualTo(NAME_FIELD, publisher.name)
                .whereEqualTo(EMAIL_ADDRESS_FIELD, publisher.emailAddress)
                .whereEqualTo(MEMBER_SINCE_FIELD, publisher.memberSince)
                .get()
                .addOnSuccessListener { documents ->
                    if (continuation.isActive) {
                        Timber.d("Returned publisher document successfully, doc: ${documents.documents[0]}")
                        val publisherDoc = documents.documents[0].toObject(Publisher::class.java)

                        if (publisherDoc == null) {
                            Timber.d("Couldn't convert firestore model to publisher model, or publisher isn't exist")
                            return@addOnSuccessListener continuation.resumeWithException(NullPointerException())
                        }

                        continuation.resume(Result.Success(publisherDoc.id))
                    } else {
                        Timber.d("continuation is no longer active")
                    }
                }.addOnFailureListener {
                    Timber.d("Failed to get Publisher id For publisher: $publisher, cause: ${it.cause}")
                    continuation.resumeWithException(it)
                }
        }
    }

    override suspend fun getPublisher(id: publisherId): Result<Publisher> {
        return wrapInCoroutineCancellable(ioDispatcher) { continuation ->
            firestore.collection(PUBLISHERS_COLLECTION)
                .document(id)
                .get()
                .addOnSuccessListener { publisherDoc ->
                    if (continuation.isActive) {
                        Timber.d("Returned publisher Info successfully, doc: ${publisherDoc.data}")
                        val publisher = publisherDoc.toObject(Publisher::class.java)

                        if (publisher == null) {
                            Timber.d("Couldn't convert firestore model to publisher model, or publisher isn't exist")
                            return@addOnSuccessListener continuation.resumeWithException(NullPointerException())
                        }

                        continuation.resume(Result.Success(publisher))
                    } else {
                        Timber.d("continuation is no longer active")
                    }
                }.addOnFailureListener {
                    Timber.d("Failed to get PublisherInfo For id: $id, cause: ${it.cause}")
                    continuation.resumeWithException(it)
                }
        }
    }

    override suspend fun getPublishers(
        ids: List<publisherId>,
        numOfFollowers: Int,
        limit: Int,
        prevSnapshot: DocumentSnapshot?
    ): Result<Pair<List<Publisher>, DocumentSnapshot>> {
        return wrapInCoroutineCancellable(ioDispatcher) { continuation ->
            var query = firestore.collection(PUBLISHERS_COLLECTION)
                .whereGreaterThanOrEqualTo(FOLLOWERS_NUMBER_FIELD, numOfFollowers)
                .withIds(ids, ID_FIELD)
                .limit(limit.toLong())
            query = if (prevSnapshot != null) query.startAfter(prevSnapshot) else query
            query.get()
                .addOnSuccessListener { publishersDocs ->
                    if (continuation.isActive) {
                        Timber.d("Returned publishers with NumOfFollowers: $numOfFollowers successfully, docs: ${publishersDocs.documents}")
                        val publishers = publishersDocs.toObjects(Publisher::class.java)

                        if (publishers.isNullOrEmpty()) {
                            Timber.d("Couldn't convert firestore model to publishers model, or publishers isn't exist")
                            return@addOnSuccessListener continuation.resume(Result.Success(Pair(emptyList(), publishersDocs.documents.last())))
                        }

                        continuation.resume(Result.Success(Pair(publishers, publishersDocs.documents.last())))
                    } else {
                        Timber.d("continuation is no longer active")
                    }
                }.addOnFailureListener {
                    Timber.d("Failed to get Publishers with numOfFollowers: $numOfFollowers, cause: ${it.cause}")
                    continuation.resumeWithException(it)
                }
        }
    }

    // End of getting data
    // Beginning of setting/updating data

    override suspend fun setDisplayName(newName: String, id: publisherId): Result<Boolean> {
        return wrapInCoroutineCancellable(ioDispatcher) { continuation ->

            val data = mapOf(
                "name" to newName
            )

            firestore.collection(PUBLISHERS_COLLECTION)
                .document(id)
                .set(data, SetOptions.merge())
                .addOnSuccessListener {
                    if (continuation.isActive) {
                        Timber.d("Updating publisher name success")
                        continuation.resume(Result.Success(true))
                    } else {
                        Timber.d("continuation is no longer active")
                    }
                }.addOnFailureListener {
                    Timber.d("Failed to update Publisher name For id: $id, cause: ${it.cause}")
                    continuation.resumeWithException(it)
                }
        }
    }

    override suspend fun addNewArticleId(articleID: articleId, publisherID: publisherId): Result<Boolean> {
        return updateArray(PUBLISHED_ARTICLES_FIELD, articleID, publisherID)
    }

    override suspend fun removeExistingArticleId(articleID: articleId, publisherID: publisherId): Result<Boolean> {
        return updateArray(PUBLISHED_ARTICLES_FIELD, articleID, publisherID, false)
    }

    override suspend fun addNewCategoryId(categoryID: String, publisherID: publisherId): Result<Boolean> {
        return updateArray(FOLLOWED_CATEGORIES_FIELD, categoryID, publisherID)
    }

    override suspend fun unFollowExistingCategoryId(categoryID: String, publisherID: publisherId): Result<Boolean> {
        return updateArray(FOLLOWED_CATEGORIES_FIELD, categoryID, publisherID, false)
    }

    /** Delegate updating arrays code to this private fun to achieve:
     *  1- Encapsulation,
     *  2- cut down the boilerplate
     */
    private suspend fun updateArray(arrayField: String, ItemId: String, publisherID: publisherId, add: Boolean = true): Result<Boolean> {
        return wrapInCoroutineCancellable(ioDispatcher) { continuation ->

            val operation = if (add) FieldValue.arrayUnion(ItemId) else FieldValue.arrayRemove(ItemId)

            // add the id of published article to array
            firestore.collection(PUBLISHERS_COLLECTION)
                .document(publisherID)
                .update(arrayField, operation)
                .addOnSuccessListener {
                    if (continuation.isActive) {
                        continuation.resume(Result.Success(true))
                    } else {
                        Timber.d("continuation is no longer active")
                    }
                }.addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    override suspend fun follow(followedPublisherID: publisherId, publisherID: publisherId): Result<Boolean> {
        return wrapInCoroutineCancellable(ioDispatcher) { continuation ->

            val pubDoc = firestore.collection(PUBLISHERS_COLLECTION).document(publisherID)
            val followedPubDoc = firestore.collection(PUBLISHERS_COLLECTION).document(followedPublisherID)

            // We used batchedWrites rather than transactions, cause we don't need any read operations
            firestore.runBatch { batch ->
                // Do two things atomically, first add the id of followed publisher to array
                batch.update(pubDoc, FOLLOWED_PUBLISHERS_FIELD, FieldValue.arrayUnion(followedPublisherID))

                // Second, increment num of followers to publisher who is followed
                batch.update(followedPubDoc, FOLLOWERS_NUMBER_FIELD, FieldValue.increment(1))
            }.addOnSuccessListener {
                if (continuation.isActive) {
                    Timber.d("Done following publisher with id: $followedPublisherID")
                    continuation.resume(Result.Success(true))
                } else {
                    Timber.d("continuation is no longer active")
                }
            }.addOnFailureListener {
                Timber.d("Failed to Follow publisher with id: $followedPublisherID, cause: ${it.cause}")
                continuation.resumeWithException(it)
            }
        }
    }

    override suspend fun unFollow(unFollowedPublisherID: publisherId, publisherID: publisherId): Result<Boolean> {
        return wrapInCoroutineCancellable(ioDispatcher) { continuation ->

            val pubDoc = firestore.collection(PUBLISHERS_COLLECTION).document(publisherID)
            val unFollowedPubDoc = firestore.collection(PUBLISHERS_COLLECTION).document(unFollowedPublisherID)

            firestore.runTransaction { transaction ->
                // All read operations must happen before writing
                val numOfFollowersValue = transaction.get(unFollowedPubDoc).get(FOLLOWERS_NUMBER_FIELD)
                val newFollowersValue = (numOfFollowersValue as? Int)?.minus(1)
                // start writing
                transaction.update(pubDoc, FOLLOWED_PUBLISHERS_FIELD, FieldValue.arrayRemove(unFollowedPublisherID))
                transaction.update(unFollowedPubDoc, FOLLOWERS_NUMBER_FIELD, newFollowersValue)
            }.addOnSuccessListener {
                if (continuation.isActive) {
                    Timber.d("Done unFollowing publisher with id: $unFollowedPublisherID")
                    continuation.resume(Result.Success(true))
                } else {
                    Timber.d("continuation is no longer active")
                }
            }.addOnFailureListener {
                Timber.d("Failed to unFollow publisher with id: $unFollowedPublisherID, cause: ${it.cause}")
                continuation.resumeWithException(it)
            }
        }
    }

    companion object {
        const val PUBLISHERS_COLLECTION = "publishers"
        const val NAME_FIELD = "name"
        const val ID_FIELD = "id"
        const val EMAIL_ADDRESS_FIELD = "emailAddress"
        const val MEMBER_SINCE_FIELD = "memberSince"
        const val PUBLISHED_ARTICLES_FIELD = "publishedArticlesIds"
        const val FOLLOWED_CATEGORIES_FIELD = "followedCategoriesIds"
        const val FOLLOWED_PUBLISHERS_FIELD = "followedPublishersIds"
        const val FOLLOWERS_NUMBER_FIELD = "numOfFollowers"
    }
}
