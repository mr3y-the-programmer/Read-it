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
     * get the name which will be displayed to other users
     */
    suspend fun getDisplayName(id: publisherId): Result<String>

    //TODO: update return type
    /**
     * get Uri of img stored in firestore
     */
    suspend fun getProfileImgUri(id: publisherId): Result<Uri>

    /**
     * get registered user email address
     */
    suspend fun getEmailAddress(id: publisherId): Result<String>

    /**
     * the first time he signed, joined app
     */
    suspend fun getCreatedSince(id: publisherId): Result<Long>

    /**
     * ids of articles he published
     */
    suspend fun getArticlesPublishedIds(id: publisherId): Result<List<articleId>>

    /**
     * ids of categories he followed
     */
    suspend fun getFollowedCategoriesIds(id: publisherId): Result<List<String>>

    /**
     * number of followers
     */
    suspend fun getFollowersNumber(id: publisherId): Result<Int>

    /**
     * update publisher/user name
     *
     * @return true on success, otherwise false
     */
    suspend fun setDisplayName(newName: String, id: publisherId): Result<Boolean>

    //TODO: update param type
    /**
     * Update profileImg, nullability here means to use a placeholder or img used in sign-in identity provider
     *
     * @return true on success, otherwise false
     */
    suspend fun setProfileImg(newImage: Byte?, id: publisherId): Result<Boolean>

    /**
     * when publishing new article, add its id to publisher profile
     *
     * @return true on success, otherwise false
     */
    suspend fun addNewArticleId(articleID: articleId, publisherID: publisherId): Result<Boolean>

    /**
     * when following new category, add its id to publisher profile
     *
     * @return true on success, otherwise false
     */
    suspend fun addNewCategoryId(categoryID: String, publisherID: publisherId): Result<Boolean>

    /**
     * Increment number of followers, and add new follower to existing followers to be able to see his articles
     *
     * @return true on success, otherwise false
     */
    suspend fun follow(followedPublisherID: publisherId, publisherID: publisherId): Result<Boolean>

    /**
     * decrement number of followers, and remove follower from existing followers
     *
     * @return true on success, otherwise false
     */
    suspend fun unFollow(unFollowedPublisherID: publisherId, publisherID: publisherId): Result<Boolean>
}