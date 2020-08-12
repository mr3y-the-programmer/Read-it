/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.paging

import androidx.paging.PagingSource
import com.google.firebase.firestore.DocumentSnapshot
import com.secret.readit.core.data.articles.content.ContentDataSource
import com.secret.readit.core.result.Result
import com.secret.readit.core.result.succeeded
import com.secret.readit.model.Article
import com.secret.readit.model.Content
import com.secret.readit.model.publisherId

/**
 * take parameters that customize the query/result returned from data source,
 *
 * also it takes [contentLimit] as a parameter to download limited num of content to de displayed like a summery
 */
data class RequestParams(val limit: Int,
                                  val appreciateNum: Int,
                                  val categoriesIds: List<String>,
                                  val withMinutesRead: Int,
                                  val mostFollowedPubsId: List<publisherId>,
                                  val specificPub: Pair<publisherId, Long>,
                                  val contentLimit: Int)

typealias ArticleWithContent = Pair<Article, Content>

//Handle boilerplate of filling page with the correct data
internal suspend fun process(result: Result<Pair<List<Article>, DocumentSnapshot>>,
                            params: PagingSource.LoadParams<DocumentSnapshot>,
                            contentSource: ContentDataSource,
                            contentLimit: Int): PagingSource.LoadResult<DocumentSnapshot, ArticleWithContent> {
    var lastSnapshot = params.key
    val data = checkIfSuccessful(result)?.let {
        val articles = it.first
        lastSnapshot = it.second
        val contents = mutableListOf<Content>()
        run {
            articles.forEach { article ->
                val loadContent = contentSource.getContent(article.id, contentLimit)
                if (loadContent.succeeded) contents.add(Content((loadContent as Result.Success).data)) else return@run
            }
        }
        articles.zip(contents)
    } ?: return PagingSource.LoadResult.Error(Exception()) //result didn't succeed, this can be interpreted by Ui consumers as LoadState.Error
    return PagingSource.LoadResult.Page(data, prevKey = null /*We don't support loading before current page yet*/, nextKey = lastSnapshot)
}

private fun checkIfSuccessful(result: Result<Pair<List<Article>, DocumentSnapshot>>) = if (result != null && result.succeeded) (result as Result.Success).data else null

fun emptyReq() = RequestParams(0, 0, emptyList(), 0, emptyList(), Pair("", -1), 0)