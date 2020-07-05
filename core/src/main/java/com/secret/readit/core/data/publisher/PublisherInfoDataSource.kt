/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.publisher

import android.net.Uri
import com.secret.readit.core.result.Result
import com.secret.readit.model.articleId
import com.secret.readit.model.publisherId

/**
 * Blueprint for publisher Information stored in firestore
 */
interface PublisherInfoDataSource {

    /**
     * get UniqueId of registered user
     */
    suspend fun getId(): Result<String>

    /**
     * get the name which will be displayed to other users
     */
    suspend fun getDisplayName(): Result<String>

    //TODO: update return type
    /**
     * get Uri of img stored in firestore
     */
    suspend fun getProfileImgUri(): Result<Uri>

    /**
     * get registered user email address
     */
    suspend fun getEmailAddress(): Result<String>

    /**
     * the first time he signed, joined app
     */
    suspend fun getCreatedSince(): Result<Long>

    /**
     * ids of articles he published
     */
    suspend fun getArticlesPublishedIds(): Result<List<articleId>>

    /**
     * ids of categories he followed
     */
    suspend fun getFollowedCategoriesIds(): Result<List<String>>

    /**
     * number of followers
     */
    suspend fun getFollowersNumber(): Result<Int>

    /**
     * update publisher/user name
     *
     * @return true on success, otherwise false
     */
    suspend fun setDisplayName(newName: String): Result<Boolean>

    //TODO: update param type
    /**
     * Update profileImg, nullability here means to use a placeholder or img used in sign-in identity provider
     *
     * @return true on success, otherwise false
     */
    suspend fun setProfileImg(newImage: Byte?): Result<Boolean>

    /**
     * when publishing new article, add its id to publisher profile
     *
     * @return true on success, otherwise false
     */
    suspend fun addNewArticleId(id: articleId): Result<Boolean>

    /**
     * when following new category, add its id to publisher profile
     *
     * @return true on success, otherwise false
     */
    suspend fun addNewCategoryId(id: String): Result<Boolean>

    /**
     * Increment number of followers, and add new follower to existing followers to be able to see his articles
     *
     * @return true on success, otherwise false
     */
    suspend fun follow(id: publisherId): Result<Boolean>

    /**
     * decrement number of followers, and remove follower from existing followers
     *
     * @return true on success, otherwise false
     */
    suspend fun unFollow(id: publisherId): Result<Boolean>
}