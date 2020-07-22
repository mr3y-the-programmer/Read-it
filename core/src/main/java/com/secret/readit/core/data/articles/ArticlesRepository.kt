/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles

import androidx.annotation.VisibleForTesting
import com.google.firebase.firestore.DocumentSnapshot
import com.secret.readit.core.data.articles.utils.Formatter
import com.secret.readit.core.data.utils.CustomIDHandler
import com.secret.readit.core.result.Result
import com.secret.readit.core.result.succeeded
import com.secret.readit.model.*
import java.lang.IllegalArgumentException
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
    private val formatter: Formatter,
    private val idHandler: CustomIDHandler = CustomIDHandler()
) {

    /** Exposed APIs For consumers to get articles based on these attributes */
    suspend fun getMostAppreciatedArticles(limit: Int, appreciateNum: Int): List<Article> {
        return getNewArticles(limit, appreciateNum)
    }

    suspend fun getMostFollowedPublishersArticles(limit: Int, pubsIds: List<publisherId>): List<Article> {
        return getNewArticles(limit, mostFollowedPubsId = pubsIds)
    }

    suspend fun getShortAndAppreciatedArticles(limit: Int, maximumMinutesRead: Int, appreciateNum: Int): List<Article> {
        return getNewArticles(limit, appreciateNum = appreciateNum, withMinutesRead = maximumMinutesRead)
    }

    suspend fun getArticlesWhichHaveCategories(limit: Int, categoriesIds: List<String>): List<Article> {
        return getNewArticles(limit, categoriesIds = categoriesIds)
    }

    suspend fun getPubArticlesSince(pubId: publisherId, since: Long): List<Article> {
        return getNewArticles(limit = 0, specificPub = Pair(pubId, since))
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
    ): List<Article> {
        val articles = mutableListOf<Article>()
        if (specificPub.first.isNotEmpty()) {
            val articlesResult = articlesDataSource.getPubArticles(specificPub, prevSnapshot)
            articles.addAll(formatter.formatPubArticles(articlesResult))
            prevSnapshot = if (articlesResult.succeeded) (articlesResult as Result.Success).data.second else prevSnapshot
        } else {
            val articlesResult = articlesDataSource.getArticles(limit, appreciateNum, categoriesIds, withMinutesRead, mostFollowedPubsId)
            articles.addAll(formatter.formatArticles(articlesResult))
        }
        return articles
    }

    /**
     * get Article with this specified id
     *
     * @return valid article or empty article if data source failed
     */
    suspend fun getArticle(id: articleId): Article {
        val articleResult = articlesDataSource.getArticle(id)
        val formattedArticle = formatter.formatArticles(articleResult, true)
        if (formattedArticle.isNullOrEmpty()) {
            return getEmptyArticle()
        }
        return formattedArticle[0]
    }

    /**
     * Publish this article, add it to firestore
     *
     * @return true on success, false on data source failure like: No Internet connection or adding invalid article
     */
    suspend fun addArticle(article: Article): Boolean {
        var successful = false
        val id = try {
            idHandler.getID(article)
        } catch (ex: IllegalArgumentException) {
            return false
        }
        val deFormattedElements = formatter.deFormatElements(id, article.content.elements)
        val result = articlesDataSource.addArticle(article.copy(id = id, content = Content(deFormattedElements)))

        if (result != null && result.succeeded) {
            successful = (result as Result.Success).data
        }
        return successful
    }

    private fun getEmptyArticle(): Article {
        val publisher = Publisher("", "", "", memberSince = -1)
        return Article("", "", Content(emptyList()), publisher, 0, 0, emptyList(), category = emptyList())
    }

    companion object {
        const val PLACE_HOLDER_URL = "https://firebasestorage.googleapis.com/v0/b/read-it-b9c8b.appspot.com" +
            "/o/articles%2Fplace_holder_image.png?alt=media&token=fd6b444e-0115-4f40-8b8d-f6deaf238179"
    }
}
