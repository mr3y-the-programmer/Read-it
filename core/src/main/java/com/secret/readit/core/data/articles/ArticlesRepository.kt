/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.secret.readit.core.data.articles.comments.CommentDataSource
import com.secret.readit.core.data.articles.content.ContentDataSource
import com.secret.readit.core.data.articles.utils.Formatter
import com.secret.readit.core.di.HomeFeedSource
import com.secret.readit.core.di.PubArticlesSource
import com.secret.readit.core.paging.BasePagingSource
import com.secret.readit.core.paging.articles.ArticleWithContent
import com.secret.readit.core.paging.articles.RequestParams
import com.secret.readit.core.remoteconfig.RemoteConfigSource
import com.secret.readit.core.result.Result
import com.secret.readit.core.result.succeeded
import com.secret.readit.core.uimodels.UiArticle
import com.secret.readit.core.uimodels.UiComment
import com.secret.readit.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single Source Of Truth for articles data, Any consumers should consume from it not from data sources directly.
 *
 * Rule: -forward actions to dataSource when needed(i.e: loading new data), it do this by Using PagingSource For caching
 *       And Use the dataSource directly in upload operations cause it doesn't need cache
 */
@Singleton
class ArticlesRepository @Inject constructor(
    private val articlesDataSource: ArticlesDataSource,
    private val contentDataSource: ContentDataSource,
    private val commentsDataSource: CommentDataSource,
    @HomeFeedSource private val articlesPagingSource: BasePagingSource<RequestParams>,
    @PubArticlesSource private val pubArticlesPagingSource: BasePagingSource<RequestParams>,
    private val formatter: Formatter,
    private val remoteConfig: RemoteConfigSource
) {

    /** Exposed APIs For consumers to get articles based on these attributes */
    suspend fun getMostAppreciatedArticles(limit: Int, appreciateNum: Int): Flow<PagingData<UiArticle>> = getNewArticles(limit, appreciateNum)

    suspend fun getMostFollowedPublishersArticles(limit: Int, pubsIds: List<publisherId>): Flow<PagingData<UiArticle>> = getNewArticles(limit, mostFollowedPubsId = pubsIds)

    suspend fun getShortAndAppreciatedArticles(limit: Int, maximumMinutesRead: Int, appreciateNum: Int): Flow<PagingData<UiArticle>> {
        return getNewArticles(limit, appreciateNum = appreciateNum, withMinutesRead = maximumMinutesRead)
    }

    suspend fun getArticlesWhichHaveCategories(limit: Int, categoriesIds: List<String>): Flow<PagingData<UiArticle>> = getNewArticles(limit, categoriesIds = categoriesIds)

    suspend fun getPubArticlesSince(pubId: publisherId, since: Long): Flow<PagingData<UiArticle>> = getNewArticles(limit = 0, specificPub = Pair(pubId, since))

    suspend fun getArticlesWithIds(ids: List<articleId>): Flow<PagingData<UiArticle>> {
        return getNewArticles(limit = 0, withIds = ids)
    }

    suspend fun appreciate(article: UiArticle): Boolean = appreciateOrDisagree(article)

    suspend fun disagree(article: UiArticle): Boolean = appreciateOrDisagree(article, false)

    /* Comments section */
    suspend fun getComments(articleID: articleId, limit: Int): List<UiComment> {
        val result = commentsDataSource.getComments(articleID, emptyList(), limit)
        return formatter.formatComments(result)
    }

    suspend fun showReplies(articleID: articleId, comment: UiComment, limit: Int): UiComment {
        val repliesResult = commentsDataSource.getComments(articleID, comment.comment.repliesIds, limit)
        return formatter.formatReplies(repliesResult, comment)
    }

    suspend fun comment(articleID: articleId, comment: UiComment): Boolean {
        val deFormattedComment = formatter.deFormatComment(comment) ?: return false
        val result = commentsDataSource.addComment(articleID, deFormattedComment)
        return checkIfSuccessful(result)
    }

    suspend fun reply(articleID: articleId, reply: UiComment, parentComment: UiComment): Boolean {
        val deFormattedReply = formatter.deFormatComment(reply) ?: return false
        val deFormattedComment = formatter.deFormatComment(parentComment) ?: return false // The parent comment need to be deFormatted in order to have a valid Id
        val result = commentsDataSource.addReply(articleID, deFormattedComment.id, deFormattedReply)
        return checkIfSuccessful(result)
    }

    // hold the current displayed Article Id,
    var currentArticleID: articleId? = null
        private set

    /* move/Encapsulate the boilerplate to this function */
    private suspend fun getNewArticles(
        limit: Int,
        appreciateNum: Int = 0,
        categoriesIds: List<String> = emptyList(),
        withMinutesRead: Int = 999,
        mostFollowedPubsId: List<publisherId> = emptyList(),
        specificPub: Pair<publisherId, Long> = Pair("", -1),
        withIds: List<articleId> = emptyList()
    ): Flow<PagingData<UiArticle>> {
        val parameters = RequestParams(
            limit,
            appreciateNum,
            categoriesIds,
            withMinutesRead,
            mostFollowedPubsId,
            specificPub,
            articleIds = withIds,
            contentLimit = remoteConfig.contentLimit.value.toInt()
        )
        val pagingSource = if ((specificPub.first.isNotEmpty() && specificPub.second > 0) || withIds.isNotEmpty()) {
            pubArticlesPagingSource.withParams<ArticleWithContent>(parameters)
        } else {
            articlesPagingSource.withParams(parameters)
        }
        return Pager(
            config = if (limit <= 0) PagingConfig(remoteConfig.pageConfigSizeLimit.value.toInt()) else PagingConfig(limit),
            pagingSourceFactory = { pagingSource }
        ).flow.map {
            formatter.formatArticles(it)
        }
    }

    /**
     * getFullArticle fun which called to load/format the full article like: clicking on article on homefeed to display its full content
     * @return the UiArticle with full content formatted
     */
    // TODO: make this fun handle refreshing, so it fetches updated article from data Source
    suspend fun getFullArticle(partialArticle: UiArticle): UiArticle {
        return getFullContent(partialArticle.article.id).let {
            val elements = formatter.formatContent(it)
            partialArticle.copy(fullContent = Content(elements))
        }.also {
            currentArticleID = it.article.id
        }
    }

    private suspend fun getFullContent(id: articleId): Content {
        val result = contentDataSource.getContent(id, 0)
        return if (result != null && result.succeeded) Content((result as Result.Success).data) else Content(emptyList())
    }

    /**
     * Publish this article, add it to firestore
     *
     * @return true on success, false on data source failure like: No Internet connection or adding invalid article(deFormatting Error)
     */
    suspend fun addArticle(uiArticle: UiArticle): Boolean {
        val deFormattingResult = formatter.deFormatArticle(uiArticle) ?: return false // Couldn't deFormat article
        val result = articlesDataSource.addArticle(deFormattingResult.first)
        if (checkIfSuccessful(result)) {
            return uploadElements(deFormattingResult.first.id, deFormattingResult.second) // If article upload succeeded, upload content
        }
        return false
    }

    private suspend fun uploadElements(id: articleId, elements: List<Element>): Boolean {
        val result = contentDataSource.addContent(id, elements)
        if (result != null && result.succeeded) {
            return (result as Result.Success).data
        }
        return false
    }

    private suspend fun appreciateOrDisagree(article: UiArticle, appreciate: Boolean = true): Boolean {
        val result = if (appreciate) articlesDataSource.incrementAppreciation(article.article.id) else articlesDataSource.incrementDisagree(article.article.id)
        return checkIfSuccessful(result)
    }

    private fun checkIfSuccessful(result: Result<Boolean>) = if (result != null && result.succeeded) (result as Result.Success).data else false

    companion object {
        const val PLACE_HOLDER_URL = "https://firebasestorage.googleapis.com/v0/b/read-it-b9c8b.appspot.com" +
            "/o/articles%2Fplace_holder_image.png?alt=media&token=fd6b444e-0115-4f40-8b8d-f6deaf238179" // FIXME: Replace with one in remote config
    }
}
