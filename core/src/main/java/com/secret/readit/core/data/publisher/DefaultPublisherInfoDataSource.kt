/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.publisher

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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
class DefaultPublisherInfoDataSource @Inject constructor(private val firestore: FirebaseFirestore,
                                                         @IoDispatcher private val ioDispatcher: CoroutineDispatcher): PublisherInfoDataSource {

    //TODO: move constants to companion object
    //TODO: delegate all set logic fun to unified private fun
    override suspend fun getPublisher(id: publisherId): Result<Publisher> {
        return wrapInCoroutineCancellable(ioDispatcher){ continuation ->
            firestore.collection("publishers")
                .document(id)
                .get()
                .addOnSuccessListener { publisherDoc ->
                    if (continuation.isActive){
                        Timber.d("Returned publisher Info successfully, doc: ${publisherDoc.data}")
                        val publisher = publisherDoc.toObject(Publisher::class.java)

                        if (publisher == null){
                            Timber.d("Couldn't convert firestore model to publisher model, or publisher isn't exist")
                            return@addOnSuccessListener continuation.resumeWithException(NullPointerException())
                        }

                        continuation.resume(Result.Success(publisher))
                    }else {
                        Timber.d("continuation is no longer active")
                    }
                }.addOnFailureListener {
                    Timber.d("Failed to get PublisherInfo For id: $id, cause: ${it.cause}")
                    continuation.resumeWithException(it)
                }
        }
    }

    //End of getting data
    //Beginning of setting/updating data

    override suspend fun setDisplayName(newName: String, id: publisherId): Result<Boolean> {
        return wrapInCoroutineCancellable(ioDispatcher){ continuation ->

            val data = mapOf(
                "name" to newName
            )

            firestore.collection("publishers")
                .document(id)
                .set(data, SetOptions.merge())
                .addOnSuccessListener {
                    if (continuation.isActive){
                        Timber.d("Updating publisher name success")
                        continuation.resume(Result.Success(true))
                    }else {
                        Timber.d("continuation is no longer active")
                    }
                }.addOnFailureListener {
                    Timber.d("Failed to update Publisher name For id: $id, cause: ${it.cause}")
                    continuation.resumeWithException(it)
                }
        }
    }

    override suspend fun setProfileImg(newImage: Byte?, id: publisherId): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun addNewArticleId(articleID: articleId, publisherID: publisherId): Result<Boolean> {
        return wrapInCoroutineCancellable(ioDispatcher){ continuation ->

            //add the id of published article to array
            firestore.collection("publishers")
                .document(publisherID)
                .update("publishedArticlesIds", FieldValue.arrayUnion(articleID))
                .addOnSuccessListener {
                    if (continuation.isActive){
                        Timber.d("Added new Article id")
                        continuation.resume(Result.Success(true))
                    }else {
                        Timber.d("continuation is no longer active")
                    }
                }.addOnFailureListener {
                    Timber.d("Failed to add new article id, cause: ${it.cause}")
                    continuation.resumeWithException(it)
                }
        }
    }

    override suspend fun addNewCategoryId(categoryID: String, publisherID: publisherId): Result<Boolean> {
        return wrapInCoroutineCancellable(ioDispatcher){ continuation ->

            //add the id of published article to array
            firestore.collection("publishers")
                .document(publisherID)
                .update("followedCategoriesIds", FieldValue.arrayUnion(categoryID))
                .addOnSuccessListener {
                    if (continuation.isActive){
                        Timber.d("Added new Category id")
                        continuation.resume(Result.Success(true))
                    }else {
                        Timber.d("continuation is no longer active")
                    }
                }.addOnFailureListener {
                    Timber.d("Failed to add new Category id, cause: ${it.cause}")
                    continuation.resumeWithException(it)
                }
        }
    }

    override suspend fun follow(followedPublisherID: publisherId, publisherID: publisherId): Result<Boolean> {
        return wrapInCoroutineCancellable(ioDispatcher) { continuation ->

            val pubDoc = firestore.collection("publishers").document(publisherID)
            val followedPubDoc = firestore.collection("publishers").document(followedPublisherID)

            //We used batchedWrites rather than transactions, cause we don't need any read operations
            firestore.runBatch { batch ->
                //Do two things atomically, first add the id of followed publisher to array
                batch.update(pubDoc, "followedPublishersIds", FieldValue.arrayUnion(followedPublisherID))

                //Second, increment num of followers to publisher who is followed
                batch.update(followedPubDoc, "numOfFollowers", FieldValue.increment(1))
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

            val pubDoc = firestore.collection("publishers").document(publisherID)
            val unFollowedPubDoc = firestore.collection("publishers").document(unFollowedPublisherID)

            firestore.runTransaction { transaction ->
                //All read operations must happen before writing
                val numOfFollowersValue = transaction.get(unFollowedPubDoc).get("numOfFollowers")
                val newFollowersValue = (numOfFollowersValue as? Int)?.minus(1)
                //start writing
                transaction.update(pubDoc, "followedPublishersIds", FieldValue.arrayRemove(unFollowedPublisherID))
                transaction.update(unFollowedPubDoc, "numOfFollowers", newFollowersValue)
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

    /*private suspend fun updatePubData(newName: String?, newImage: Byte?,
                                      articleID: articleId?, categoryID: String?, id: publisherId){
        val updatedData = mapOf(
            "name" to newName,
            "profileImg" to newImage,
            ""
        )

    }*/
}