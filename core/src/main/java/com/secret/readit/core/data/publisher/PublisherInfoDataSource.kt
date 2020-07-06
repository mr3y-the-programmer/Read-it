/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.publisher

import com.secret.readit.core.result.Result
import com.secret.readit.model.Publisher
import com.secret.readit.model.articleId
import com.secret.readit.model.publisherId

/**
 * Blueprint for publisher Information stored in firestore
 */
interface PublisherInfoDataSource {

    /**
     * get All Publisher Info as a bundle cause we can't retrieve partial document in firestore
     */
    suspend fun getPublisher(id: publisherId): Result<Publisher>

    /**
     * update publisher/user name
     *
     * @return true on success, otherwise false
     */
    suspend fun setDisplayName(newName: String, id: publisherId): Result<Boolean>

    // TODO: update param type
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
     * remove existing article id from publisher profile
     *
     * @return true on success, otherwise false
     */
    suspend fun removeExistingArticleId(articleID: articleId, publisherID: publisherId): Result<Boolean>

    /**
     * when following new category, add its id to publisher profile
     *
     * @return true on success, otherwise false
     */
    suspend fun addNewCategoryId(categoryID: String, publisherID: publisherId): Result<Boolean>

    /**
     * unfollowing category, remove its id from publisher profile
     *
     * @return true on success, otherwise false
     */
    suspend fun unFollowExistingCategoryId(categoryID: String, publisherID: publisherId): Result<Boolean>

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
