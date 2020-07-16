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
     * Basically, this function gets a number of articles from firestore (i.e: 50),
     * cause fetching entire articles in firestore is waste of network and quota and will never needed by user
     */
    suspend fun getArticles(): Result<List<Article>>

    /**
     * get specific article by [id]
     */
    suspend fun getArticle(id: articleId): Result<Article>

    /**
     * toggle bookmark state to article with specified id
     *
     * @return true on Success, otherwise returns false
     *///Cancelled for now until solving some problems
    /*suspend fun bookmark(id: articleId, bookmark: Boolean = true): Result<Boolean>*/

    /**
     * add the [article] to firestore
     */
    suspend fun addArticle(article: Article): Result<Boolean>
}
