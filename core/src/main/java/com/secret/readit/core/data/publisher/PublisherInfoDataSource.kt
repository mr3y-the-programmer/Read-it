/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.publisher

import android.net.Uri
import com.secret.readit.model.articleId
import com.secret.readit.model.publisherId

/**
 * Blueprint for publisher Information stored in firestore
 */
interface PublisherInfoDataSource {

    /**
     * get UniqueId of registered user
     */
    suspend fun getId(): String

    /**
     * get the name which will be displayed to other users
     */
    suspend fun getDisplayName(): String

    //TODO: update return type
    /**
     * get Uri of img stored in firestore
     */
    suspend fun getProfileImgUri(): Uri

    /**
     * get registered user email address
     */
    suspend fun getEmailAddress(): String

    /**
     * the first time he signed, joined app
     */
    suspend fun getCreatedSince(): Long

    /**
     * ids of articles he published
     */
    suspend fun getArticlesPublishedIds(): List<String>

    /**
     * ids of categories he followed
     */
    suspend fun getFollowedCategoriesIds(): List<String>

    /**
     * number of followers
     */
    suspend fun getFollowersNumber(): Int

    /**
     * update publisher/user name
     */
    suspend fun setDisplayName(newName: String)

    //TODO: update param type
    /**
     * Update profileImg, nullability here means to use a placeholder or img used in sign-in identity provider
     */
    suspend fun setProfileImg(newImage: Byte?)

    /**
     * when publishing new article, add its id to publisher profile
     */
    suspend fun addNewArticleId(id: articleId)

    /**
     * when following new category, add its id to publisher profile
     */
    suspend fun addNewCategoryId(id: String)

    /**
     * Increment number of followers, and add new follower to existing followers to be able to see his articles
     */
    suspend fun follow(id: publisherId)

    /**
     * decrement number of followers, and remove follower from existing followers
     */
    suspend fun unFollow(id: publisherId)
}