/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.auth

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.internal.api.FirebaseNoSignedInUserException
import com.secret.readit.core.data.utils.wrapInCoroutineCancellable
import com.secret.readit.core.di.IoDispatcher
import com.secret.readit.core.result.Result
import com.secret.readit.model.articleId
import com.secret.readit.model.publisherId
import kotlinx.coroutines.CoroutineDispatcher
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * AuthRepository has one Responsibility:
 *
 * store data from [DefaultAuthDataSource] in firestore's publishers collection, so we can get data from single source of truth
 */
@Singleton
class AuthRepository @Inject constructor(
    private val dataSource: AuthDataSource,
    private val firestore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    /**
     * This fun gets called when user registeres/login, joins the app
     * check first if there's a doc for user(i.e User joined before).
     * Otherwise this is the first time User Joins So create a doc for him
     * @return true on (success && this is the first time), otherwise false
     */
    suspend fun createDocIfPossible(): Result<Boolean> {
        if (!dataSource.isUserSignedIn()) {
            Timber.e("Cannot store user data because he hasn't signed-in")
            return Result.Error(FirebaseNoSignedInUserException("USER MUST BE SIGNED IN FIRST"))
        }
        // uid guaranteed to be non-null as long as the user is signed-in(FirebaseUser != null)
        val uid = getId()!!

        if (!(check(uid) as Result.Success).data) {
            return createDoc(uid)
        }
        return Result.Success(false)
    }

    /**
     * check if user doc exist on firetore or not
     * if doc exist @return true, doesn't exist @return false
     */
    private suspend fun check(id: publisherId): Result<Boolean> {
        return wrapInCoroutineCancellable(ioDispatcher) { continuation ->
            firestore.collection(PUBLISHERS_COLLECTION)
                .document(id)
                .get()
                .addOnSuccessListener {
                    if (continuation.isActive) {
                        if (it.exists()) {
                            Timber.d("User already signed before!!, No need to create a doc for him")
                            return@addOnSuccessListener continuation.resume(Result.Success(true))
                        }
                        return@addOnSuccessListener continuation.resume(Result.Success(false))
                    } else {
                        Timber.d("Continuation is no longer active")
                    }
                }.addOnFailureListener {
                    Timber.d("Failed to get doc with this id: $id")
                    continuation.resumeWithException(it)
                }
        }
    }

    /**
     * Create new doc with: @param id
     *
     * @return true on Success, otherwise false
     */
    private suspend fun createDoc(id: publisherId): Result<Boolean> {
        val data = mapOf(
            NAME_FIELD to dataSource.getDisplayName(),
            EMAIL_ADDRESS_FIELD to dataSource.getEmailAddress(),
            PROFILE_IMG_FIELD to dataSource.getProfileImgUri()?.toString(), // Firestore doesn't support Uri as data type
            MEMBER_SINCE_FIELD to dataSource.getCreatedSince(),
            PUBLISHED_ARTICLES_FIELD to emptyList<articleId>(),
            FOLLOWED_CATEGORIES_FIELD to emptyList<String>(),
            FOLLOWED_PUBLISHERS_FIELD to emptyList<publisherId>(),
            FOLLOWERS_NUMBER_FIELD to 0
        )

        return wrapInCoroutineCancellable(ioDispatcher) { continuation ->
            firestore.collection(PUBLISHERS_COLLECTION)
                .document(id)
                .set(data)
                .addOnSuccessListener {
                    if (continuation.isActive) {
                        Timber.d("Created doc for User with id: $id Successfully")
                        continuation.resume(Result.Success(true))
                    } else {
                        Timber.d("continuation is no longer active")
                    }
                }.addOnFailureListener {
                    Timber.d("Failed to create doc for user with id: $id")
                    continuation.resumeWithException(it)
                }
        }
    }

    /**
     * Also, When we need to get id of signed user We get this through this fun in repository,
     * not from dataSource directly
     */
    fun getId(): String? {
        return dataSource.getUid()
    }

    companion object {
        const val PUBLISHERS_COLLECTION = "publishers"
        const val PUBLISHED_ARTICLES_FIELD = "publishedArticlesIds"
        const val FOLLOWED_CATEGORIES_FIELD = "followedCategoriesIds"
        const val FOLLOWED_PUBLISHERS_FIELD = "followedPublishersIds"
        const val FOLLOWERS_NUMBER_FIELD = "numOfFollowers"
        const val NAME_FIELD = "name"
        const val EMAIL_ADDRESS_FIELD = "emailAddress"
        const val PROFILE_IMG_FIELD = "profileImg"
        const val MEMBER_SINCE_FIELD = "memberSince"
    }
}
