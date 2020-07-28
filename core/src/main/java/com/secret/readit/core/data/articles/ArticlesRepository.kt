/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles

import androidx.annotation.VisibleForTesting
import com.google.firebase.firestore.DocumentSnapshot
import com.secret.readit.core.data.articles.comments.CommentDataSource
import com.secret.readit.core.data.articles.utils.Formatter
import com.secret.readit.core.result.Result
import com.secret.readit.core.result.succeeded
import com.secret.readit.core.uimodels.UiArticle
import com.secret.readit.core.uimodels.UiComment
import com.secret.readit.model.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single Source Of Truth for articles data, Any consumers should consume from it not from data sources directly.
 *
 * Rule: -forward actions to dataSource when needed(i.e: loading new data)
 */
@Singleton
class ArticlesRepository @Inject constructor(
    private val articlesDataSource: ArticlesDataSource,
    private val commentsDataSource: CommentDataSource,
    private val formatter: Formatter
) {

    /** Exposed APIs For consumers to get articles based on these attributes */
    suspend fun getMostAppreciatedArticles(limit: Int, appreciateNum: Int): List<UiArticle> = getNewArticles(limit, appreciateNum)

    suspend fun getMostFollowedPublishersArticles(limit: Int, pubsIds: List<publisherId>): List<UiArticle> = getNewArticles(limit, mostFollowedPubsId = pubsIds)

    suspend fun getShortAndAppreciatedArticles(limit: Int, maximumMinutesRead: Int, appreciateNum: Int): List<UiArticle> {
        return getNewArticles(limit, appreciateNum = appreciateNum, withMinutesRead = maximumMinutesRead)
    }

    suspend fun getArticlesWhichHaveCategories(limit: Int, categoriesIds: List<String>): List<UiArticle> = getNewArticles(limit, categoriesIds = categoriesIds)

    suspend fun getPubArticlesSince(pubId: publisherId, since: Long): List<UiArticle> = getNewArticles(limit = 0, specificPub = Pair(pubId, since))

    suspend fun appreciate(article: UiArticle): Boolean = appreciateOrDisagree(article)

    suspend fun disagree(article: UiArticle): Boolean = appreciateOrDisagree(article, false)

    /* Comments section */
    suspend fun getComments(article: UiArticle, limit: Int): List<UiComment> {
        val result = commentsDataSource.getComments(article.article.id, emptyList(), limit)
        return formatter.formatComments(result)
    }

    suspend fun showReplies(article: UiArticle, comment: UiComment, limit: Int): UiComment {
        val repliesResult = commentsDataSource.getComments(article.article.id, comment.comment.repliesIds, limit)
        return formatter.formatReplies(repliesResult, comment)
    }

    suspend fun comment(article: UiArticle, comment: UiComment): Boolean {
        val deFormattedComment = formatter.deFormatComment(comment) ?: return false
        val result = commentsDataSource.addComment(article.article.id, deFormattedComment)
        return checkIfSuccessful(result)
    }

    suspend fun reply(article: UiArticle, reply: UiComment, parentComment: UiComment): Boolean {
        val deFormattedReply = formatter.deFormatComment(reply) ?: return false
        val deFormattedComment = formatter.deFormatComment(parentComment) ?: return false //The parent comment need to be deFormatted in order to have a valid Id
        val result = commentsDataSource.addReply(article.article.id, deFormattedComment.id, deFormattedReply)
        return checkIfSuccessful(result)
    }
    //hold last document snapshot in-Memory to be able to get queries after it and avoid leaking resources and money
    @VisibleForTesting
    var prevSnapshot: DocumentSnapshot? = null
        private set
    /**
     * move/Encapsulate the boilerplate to this function that is only public for sake of testing,
     * **IMPORTANT NOTE**: Consumers mustn't call this directly, instead Use one of [getMostAppreciatedArticles], [getMostFollowedPublishersArticles]...etc
     *
     * We are Using @VisibleForTesting here to guarantee encapsulation
     * Other Solutions would be: 1- using reflection , make a custom lint rule with high penalty on calling this function like raising a compiler error
     */
    @VisibleForTesting
    suspend fun getNewArticles(
        limit: Int,
        appreciateNum: Int = 0,
        categoriesIds: List<String> = emptyList(),
        withMinutesRead: Int = 999,
        mostFollowedPubsId: List<publisherId> = emptyList(),
        specificPub: Pair<publisherId, Long> = Pair("", -1)
    ): List<UiArticle> {
        val articles = mutableListOf<UiArticle>()
        if (specificPub.first.isNotEmpty() && specificPub.second > 0) {
            val articlesResult = articlesDataSource.getPubArticles(specificPub, prevSnapshot)
            articles.addAll(formatter.formatPubArticles(articlesResult, CONTENT_DISPLAYED_LIMIT))
            prevSnapshot = if (articlesResult.succeeded) (articlesResult as Result.Success).data.second else prevSnapshot
        } else {
            val articlesResult = articlesDataSource.getArticles(limit, appreciateNum, categoriesIds, withMinutesRead, mostFollowedPubsId)
            articles.addAll(formatter.formatArticles(articlesResult, CONTENT_DISPLAYED_LIMIT))
        }
        return articles
    }

    /**
     * getFullArticle fun which called to load/format the full article like: clicking on article on homefeed to display its full content
     * @return the UiArticle with full content formatted
     */
    suspend fun getFullArticle(partialArticle: UiArticle): UiArticle = formatter.formatFullArticle(partialArticle)

    /**
     * Publish this article, add it to firestore
     *
     * @return true on success, false on data source failure like: No Internet connection or adding invalid article(deFormatting Error)
     */
    suspend fun addArticle(uiArticle: UiArticle): Boolean {
        val deFormattingResult = formatter.deFormatArticle(uiArticle) ?: return false //Couldn't deFormat article
        val result = articlesDataSource.addArticle(deFormattingResult.first)
        if (checkIfSuccessful(result)) {
            return formatter.uploadElements(deFormattingResult.first.id, deFormattingResult.second) //If article upload succeeded, upload content
        }
        return false
    }
    private suspend fun appreciateOrDisagree(article: UiArticle, appreciate: Boolean = true): Boolean {
        val result = if (appreciate) articlesDataSource.incrementAppreciation(article.article.id) else articlesDataSource.incrementDisagree(article.article.id)
        return checkIfSuccessful(result)
    }

    private fun checkIfSuccessful(result: Result<Boolean>) = if (result != null && result.succeeded) (result as Result.Success).data else false

    companion object {
        const val CONTENT_DISPLAYED_LIMIT = 5 //TODO: configure it through remote config
        const val PLACE_HOLDER_URL = "https://firebasestorage.googleapis.com/v0/b/read-it-b9c8b.appspot.com" +
            "/o/articles%2Fplace_holder_image.png?alt=media&token=fd6b444e-0115-4f40-8b8d-f6deaf238179"
    }
}
