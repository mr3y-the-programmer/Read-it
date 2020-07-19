/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles

import com.secret.readit.core.result.Result
import com.secret.readit.model.Article
import com.secret.readit.model.articleId

/**
 * Blueprint for basic operations on firestore articles
 */
interface ArticlesDataSource {

    /**
     * the main function to get articles from articles collection,
     * it takes many parameters the only one needed is [limit] to avoid fetching unNeeded articles
     */
    suspend fun getArticles(limit: Int,
                            numOfAppreciation: Int = 0,
                            containCategories: List<String> = emptyList(),
                            numOfMinutesRead: Int = 0,
                            pubFollowersMoreThan: Int = 0): Result<List<Article>>

    /**
     * get specific article by [id]
     */
    suspend fun getArticle(id: articleId): Result<Article>

    /**
     * add the [article] to firestore
     */
    suspend fun addArticle(article: Article): Result<Boolean>
}
