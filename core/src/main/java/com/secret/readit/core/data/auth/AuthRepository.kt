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
     * This fun gets called only the first time user registeres, joins the app
     *
     * next time he login, his data will be already exist in firestore
     *
     * @return true on success, otherwise false
     */
    suspend fun createDocForUserInfo(): Result<Boolean> {
        if (!dataSource.isUserSignedIn()) {
            Timber.e("Cannot store user data because he hasn't signed-in")
            return Result.Error(FirebaseNoSignedInUserException("USER MUST BE SIGNED IN FIRST"))
        }
        // uid guaranteed to be non-null as long as the user is signed-in(FirebaseUser != null)
        val uid = dataSource.getUid()!!

        val data = mapOf(
            NAME_FIELD to dataSource.getDisplayName(),
            EMAIL_ADDRESS_FIELD to dataSource.getEmailAddress(),
            PROFILE_IMG_FIELD to dataSource.getProfileImgUri(),
            MEMBER_SINCE_FIELD to dataSource.getCreatedSince(),
            PUBLISHED_ARTICLES_FIELD to emptyList<articleId>(),
            FOLLOWED_CATEGORIES_FIELD to emptyList<String>(),
            FOLLOWED_PUBLISHERS_FIELD to emptyList<publisherId>(),
            FOLLOWERS_NUMBER_FIELD to 0
        )

        return wrapInCoroutineCancellable(ioDispatcher) { continuation ->
            firestore.collection(PUBLISHERS_COLLECTION)
                .document(uid)
                .set(data)
                .addOnSuccessListener {
                    if (continuation.isActive) {
                        Timber.d("Created doc for User with id: $uid Successfully")
                        continuation.resume(Result.Success(true))
                    } else {
                        Timber.d("continuation is no longer active")
                    }
                }.addOnFailureListener {
                    Timber.d("Failed to create doc for user with id: $uid")
                    continuation.resumeWithException(it)
                }
        }
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
