/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.publisher

import com.google.firebase.firestore.DocumentSnapshot
import com.secret.readit.core.result.Result
import com.secret.readit.model.Publisher
import com.secret.readit.model.articleId
import com.secret.readit.model.publisherId

/**
 * Blueprint for publisher Information stored in firestore
 */
interface PublisherInfoDataSource {

    /**
     * get Unique publisher id stored in firestore Using Publisher (name, email, creation time)
     */
    suspend fun getPublisherId(publisher: PubImportantInfo): Result<publisherId>

    /**
     * get All Publisher Info as a bundle cause we can't retrieve partial document in firestore
     */
    suspend fun getPublisher(id: publisherId): Result<Publisher>

    /**
     * the main function to get a list of publishers stored in publishers collection
     * it takes additional parameters for customizing query like [limit], [numOfFollowers], [prevSnapshot]
     */
    suspend fun getPublishers(
        ids: List<publisherId>,
        numOfFollowers: Int,
        limit: Int,
        prevSnapshot: DocumentSnapshot?
    ): Result<Pair<List<Publisher>, DocumentSnapshot>>

    /**
     * update publisher/user name
     *
     * @return true on success, otherwise false
     */
    suspend fun setDisplayName(newName: String, id: publisherId): Result<Boolean>

    /**
     * update profile img url
     *
     * @return true on success, otherwise false
     */
    suspend fun updateProfilePicUrl(newUri: String, id: publisherId): Result<Boolean>

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

    /**
     * Add Article to Bookmarks
     *
     * @return true on success, otherwise false
     */
    suspend fun bookmark(articleID: articleId, userID: publisherId): Result<Boolean>

    /**
     * Remove Article From Bookmarks
     *
     * @return true on success, otherwise false
     */
    suspend fun unBookmark(articleID: articleId, userID: publisherId): Result<Boolean>
}
