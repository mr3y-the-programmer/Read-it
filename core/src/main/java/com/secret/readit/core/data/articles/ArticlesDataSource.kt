/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles

import com.google.firebase.firestore.DocumentSnapshot
import com.secret.readit.core.result.Result
import com.secret.readit.model.Article
import com.secret.readit.model.articleId
import com.secret.readit.model.publisherId

/**
 * Blueprint for basic operations on firestore articles
 */
interface ArticlesDataSource {

    /**
     * the main function to get articles from articles collection,
     * it takes many parameters the only one needed is [limit] to avoid fetching unNeeded articles,
     * it also takes [prevSnapshot] to support pagination and avoid wasting money and resources
     * **Note**: [prevSnapshot] should be null only when it is the first time to load
     */
    suspend fun getArticles(
        limit: Int,
        numOfAppreciation: Int,
        containCategories: List<String>,
        numOfMinutesRead: Int,
        pubIds: List<publisherId>,
        prevSnapshot: DocumentSnapshot?
    ): Result<Pair<List<Article>, DocumentSnapshot>>

    /**
     * get specific article by [id]
     */
    suspend fun getArticle(id: articleId): Result<Article>

    /**
     * get Articles published by [publisherId] since period [Long]
     * it also takes [prevSnapshot] as a parameter to get data after the last doc in previous query,
     * So it avoids getting data that is previously displayed
     *
     * **NOTE**: [prevSnapshot] should be null only when it is first time loading articles
     */
    suspend fun getPubArticles(info: Pair<publisherId, Long>, ids: List<articleId>, prevSnapshot: DocumentSnapshot?): Result<Pair<List<Article>, DocumentSnapshot>>

    /**
     * add the [article] to firestore
     */
    suspend fun addArticle(article: Article): Result<Boolean>

    /**
     * increment article with [id] appreciate num
     */
    suspend fun incrementAppreciation(id: articleId): Result<Boolean>

    /**
     * increment article with [id] disagree num
     */
    suspend fun incrementDisagree(id: articleId): Result<Boolean>
}
