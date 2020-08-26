/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.publisher

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.secret.readit.core.data.utils.after
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
            firestore.collection(PUBLISHERS_COLLECTION)
                .whereGreaterThanOrEqualTo(FOLLOWERS_NUMBER_FIELD, numOfFollowers)
                .withIds(ids, ID_FIELD)
                .limit(limit.toLong())
                .after(prevSnapshot)
                .get()
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
        return update(id, Pair(NAME_FIELD, newName))
    }

    override suspend fun updateProfilePicUrl(newUri: String, id: publisherId): Result<Boolean> {
        return update(id, Pair(PROFILE_IMG_FIELD, newUri))
    }

    private suspend fun update(id: publisherId, fieldAndValue: Pair<String, String>): Result<Boolean> {
        return wrapInCoroutineCancellable(ioDispatcher) { continuation ->

            val data = mapOf(
                fieldAndValue.first to fieldAndValue.second
            )

            firestore.collection(PUBLISHERS_COLLECTION)
                .document(id)
                .set(data, SetOptions.merge())
                .addOnSuccessListener {
                    if (continuation.isActive) {
                        Timber.d("Updating publisher Info success")
                        continuation.resume(Result.Success(true))
                    } else {
                        Timber.d("continuation is no longer active")
                    }
                }.addOnFailureListener {
                    Timber.d("Failed to update Publisher Info For id: $id, cause: ${it.cause}")
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
        return updateFollow(followedPublisherID, publisherID)
    }

    override suspend fun unFollow(unFollowedPublisherID: publisherId, publisherID: publisherId): Result<Boolean> {
        return updateFollow(unFollowedPublisherID, publisherID, false)
    }

    // userID here is the one who follow or unFollow
    private suspend fun updateFollow(pubID: publisherId, userID: publisherId, positive: Boolean = true): Result<Boolean> {
        return wrapInCoroutineCancellable(ioDispatcher) { continuation ->
            val userDoc = firestore.collection(PUBLISHERS_COLLECTION).document(userID)
            val pubDoc = firestore.collection(PUBLISHERS_COLLECTION).document(pubID)
            val userOperation = if (positive) FieldValue.arrayUnion(pubID) else FieldValue.arrayRemove(pubID)
            val pubOperation = if (positive) FieldValue.arrayUnion(userID) else FieldValue.arrayRemove(userID)

            // We used batchedWrites rather than transactions, cause we don't need any read operations
            firestore.runBatch { batch ->
                // Do three things atomically, first modify followed publishers array
                batch.update(userDoc, FOLLOWED_PUBLISHERS_FIELD, userOperation)

                // Second, modify num of publisher's followers
                batch.update(pubDoc, FOLLOWERS_NUMBER_FIELD, FieldValue.increment(if (positive) 1 else -1))

                // Third, Update followersIds field
                batch.update(pubDoc, FOLLOWERS_FIELD, pubOperation)
            }.addOnSuccessListener {
                if (continuation.isActive) {
                    Timber.d("Done updating $pubID followers and $userID Following")
                    continuation.resume(Result.Success(true))
                } else {
                    Timber.d("continuation is no longer active")
                }
            }.addOnFailureListener {
                Timber.d("Failed to update $pubID Followers and $userID Following, cause: ${it.cause}")
                continuation.resumeWithException(it)
            }
        }
    }

    override suspend fun bookmark(articleID: articleId, userID: publisherId): Result<Boolean> {
        return updateArray(BOOKMARKED_ARTICLES_FIELD, articleID, userID)
    }

    override suspend fun unBookmark(articleID: articleId, userID: publisherId): Result<Boolean> {
        return updateArray(BOOKMARKED_ARTICLES_FIELD, articleID, userID, false)
    }

    companion object {
        const val PUBLISHERS_COLLECTION = "publishers"
        const val NAME_FIELD = "name"
        const val PROFILE_IMG_FIELD = "profileImgUri"
        const val ID_FIELD = "id"
        const val EMAIL_ADDRESS_FIELD = "emailAddress"
        const val MEMBER_SINCE_FIELD = "memberSince"
        const val PUBLISHED_ARTICLES_FIELD = "publishedArticlesIds"
        const val FOLLOWED_CATEGORIES_FIELD = "followedCategoriesIds"
        const val FOLLOWED_PUBLISHERS_FIELD = "followedPublishersIds"
        const val BOOKMARKED_ARTICLES_FIELD = "bookmarkedArticlesIds"
        const val FOLLOWERS_NUMBER_FIELD = "numOfFollowers"
        const val FOLLOWERS_FIELD = "followersIds"
    }
}
