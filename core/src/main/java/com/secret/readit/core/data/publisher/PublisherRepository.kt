/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.publisher

import android.graphics.Bitmap
import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.secret.readit.core.data.auth.AuthRepository
import com.secret.readit.core.data.shared.Destination
import com.secret.readit.core.data.shared.StorageRepository
import com.secret.readit.core.data.utils.CustomIDHandler
import com.secret.readit.core.di.PubProfileSource
import com.secret.readit.core.di.PublishersSource
import com.secret.readit.core.paging.BasePagingSource
import com.secret.readit.core.paging.publisher.RequestParams
import com.secret.readit.core.result.Result
import com.secret.readit.core.result.succeeded
import com.secret.readit.core.uimodels.UiArticle
import com.secret.readit.core.uimodels.UiPublisher
import com.secret.readit.model.Category
import com.secret.readit.model.Publisher
import com.secret.readit.model.publisherId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Any consumers should interact with this Repo not with DataSources directly,
 * Rule: -forward actions when needed to dataSource(Use PagingSources for caching)
 *       -provide Publisher data to consumers in expected format
 */
@Singleton
class PublisherRepository @Inject constructor(
    private val publisherDataSource: PublisherInfoDataSource,
    @PublishersSource private val pubsPagingSource: BasePagingSource<RequestParams>,
    @PubProfileSource private val pubProfilePagingSource: BasePagingSource<RequestParams>,
    private val authRepo: AuthRepository,
    private val storageRepo: StorageRepository,
    private val idHandler: CustomIDHandler = CustomIDHandler()
) {

    /**
     * get Current Signed-in publisher/user, empty Publisher if no signed-in user
     */
    suspend fun getCurrentUser(): UiPublisher {
        val id = authRepo.getId() ?: return getEmptyPublisher()
        return getPublisherInfo(id)
    }

    /**
     * update Current signed-in user name,
     * @return true on success, false on failure or no signed-in user
     */
    suspend fun updateName(newName: String): Boolean {
        val id = authRepo.getId() ?: return false // User isn't Signed-in
        val result = publisherDataSource.setDisplayName(newName, id)
        return if (result.succeeded) (result as Result.Success).data else false
    }

    suspend fun updateProfileImg(newImgPath: String): Boolean {
        val id = authRepo.getId() ?: return false // User isn't Signed-in
        val imgUri = storageRepo.uploadImg(id, newImgPath, Destination.PUBLISHER) ?: return false //Upload didn't succeed
        val result = publisherDataSource.updateProfilePicUrl(imgUri.toString(), id)
        return if (result.succeeded) (result as Result.Success).data else false
    }

    /**
     * publish new article,
     * @return true on success, false on failure or no signed-in user
     */
    suspend fun addNewArticle(article: UiArticle): Boolean = update(type = UpdateType.PUBLISH_ARTICLE, article = article)

    /**
     * remove existing article,
     * @return true on success, false on failure or no signed-in user
     */
    suspend fun removeArticle(article: UiArticle): Boolean = update(type = UpdateType.PUBLISH_ARTICLE, article = article, positive = false)

    /**
     * start following/subscribing new category
     */
    suspend fun followCategory(category: Category): Boolean = update(type = UpdateType.FOLLOW_CATEGORY, category = category)

    /**
     * UnFollow/UnSubscribe category
     */
    suspend fun unFollowCategory(category: Category): Boolean = update(type = UpdateType.FOLLOW_CATEGORY, category = category, positive = false)

    /**
     * Bookmark article
     */
    //**NOTE**: This unTested because its siblings tested well, you can add tests later
    suspend fun bookmark(article: UiArticle): Boolean = update(type = UpdateType.BOOKMARK, article = article)

    /**
     * Remove article from bookmarks
     */
    //**NOTE**: This unTested because its siblings tested well, you can add tests later
    suspend fun unBookmark(article: UiArticle): Boolean = update(type = UpdateType.BOOKMARK, article = article, positive = false)

    /**
     * follow publisher
     */
    suspend fun followPublisher(publisher: UiPublisher): Boolean = updateFollowers(publisher)

    /**
     * unFollow publisher
     */
    suspend fun unFollowPublisher(publisher: UiPublisher): Boolean = updateFollowers(publisher, positive = false)

    /** Exposed APIs For Consumers */
    suspend fun getPubsWithNumOfFollowers(followersNumber: Int, limit: Int): Flow<PagingData<UiPublisher>> = getPublishers(followersNumber = followersNumber, limit = limit)

    suspend fun getPubs(ids: List<publisherId>): Flow<PagingData<UiPublisher>> = getPublishers(ids, isPubProfile = true)

    /**private fun handles the boilerplate and connecting to dataSource*/
    private suspend fun getPublishers(
        ids: List<publisherId> = emptyList(),
        followersNumber: Int = 0,
        limit: Int = 100,
        isPubProfile: Boolean = false
    ): Flow<PagingData<UiPublisher>> {
        val params = RequestParams(limit, followersNumber, ids)
        val pagingSource = if (isPubProfile) pubProfilePagingSource.withParams<Publisher>(params) else pubsPagingSource.withParams(params)
        return Pager(
            config = PagingConfig(pageSize = limit),
            pagingSourceFactory = { pagingSource }
        ).flow.map {
            format(it)
        }
    }

    private suspend fun format(page: PagingData<Publisher>): PagingData<UiPublisher> {
        return page.map {
            val profileImg = storageRepo.downloadImg(Uri.parse(it.profileImgUri), DEFAULT_PROFILE_IMG_URL)
            UiPublisher(it, profileImg)
        }
    }

    suspend fun getPublisherInfo(id: publisherId): UiPublisher {
        val result = publisherDataSource.getPublisher(id)

        var publisher = getEmptyPublisher().publisher
        var profileImg: Bitmap? = null
        if (result.succeeded) {
            publisher = (result as Result.Success).data
            val imgUri = Uri.parse(publisher.profileImgUri)
            profileImg = storageRepo.downloadImg(imgUri, DEFAULT_PROFILE_IMG_URL)
        }
        return UiPublisher(publisher = publisher, profileImg = profileImg)
    }

    /**
     * Callers should only specify one of two params either article or category
     * @param positive when true mean adding, false mean removing
     */
    private suspend fun update(type: UpdateType, article: UiArticle? = null, category: Category? = null, positive: Boolean = true): Boolean {
        val id = authRepo.getId() ?: return false
        val result = try {
            when(type) {
                UpdateType.PUBLISH_ARTICLE -> {
                    val articleID = idHandler.getID(article!!.article)
                    if (positive) publisherDataSource.addNewArticleId(articleID, id) else publisherDataSource.removeExistingArticleId(articleID, id)
                }
                UpdateType.BOOKMARK -> {
                    val articleID = idHandler.getID(article!!.article)
                    if (positive) publisherDataSource.bookmark(articleID, id) else publisherDataSource.unBookmark(articleID, id)
                }
                UpdateType.FOLLOW_CATEGORY -> {
                    val categoryID = idHandler.getID(category!!)
                    if (positive) publisherDataSource.addNewCategoryId(categoryID, id) else publisherDataSource.unFollowExistingCategoryId(categoryID, id)
                }
            }
        } catch (ex: IllegalArgumentException) {
            Timber.e("Not Valid Article/Category")
            return false
        }
        return if (result != null && result.succeeded) (result as Result.Success).data else false
    }

    /**
     * @param positive when true mean adding, false mean removing
     */
    private suspend fun updateFollowers(publisher: UiPublisher, positive: Boolean = true): Boolean {
        val userID = authRepo.getId() ?: return false
        val firestorePub = publisher.publisher

        if (firestorePub.emailAddress.isEmpty() || firestorePub.name.isEmpty() || firestorePub.memberSince < 0) return false

        val publisherId = getPublisherId(PubImportantInfo(firestorePub.name, firestorePub.emailAddress, firestorePub.memberSince)) ?: return false

        val result = if (positive) publisherDataSource.follow(publisherId, userID) else publisherDataSource.unFollow(publisherId, userID)
        return if (result != null && result.succeeded) (result as Result.Success).data else false
    }

    suspend fun getPublisherId(pub: PubImportantInfo): publisherId? {
        val publisherIdResult = publisherDataSource.getPublisherId(pub)
        return if (publisherIdResult != null && publisherIdResult.succeeded) {
            (publisherIdResult as Result.Success).data
        } else {
            null
        }
    }

    private fun getEmptyPublisher(): UiPublisher {
        val pub = Publisher(id = "", name = "", emailAddress = "", memberSince = -1)
        return UiPublisher(publisher = pub, profileImg = null)
    }

    private enum class UpdateType{
        PUBLISH_ARTICLE,
        FOLLOW_CATEGORY,
        BOOKMARK
    }

    companion object {
        const val DEFAULT_PROFILE_IMG_URL = "https://firebasestorage.googleapis.com/v0/b/read-it-b9c8b.appspot.com/" +
            "o/publishers%2Faccount_default.png?alt=media&token=1502d0e1-c30f-4d45-997d-e39ac5af62ba" //FIXME: Use one from RemoteConfig Source
    }
}
