/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.paging.articles

import androidx.paging.PagingSource
import com.google.firebase.firestore.DocumentSnapshot
import com.secret.readit.core.data.articles.ArticlesDataSource
import com.secret.readit.core.data.articles.content.ContentDataSource
import com.secret.readit.core.paging.BasePagingSource
import javax.inject.Inject

/**
 * load articles in pages by Using Paging 3 so we can have many benefits like:
 * -in-memory cache support
 * -deduplication of requests, So each request is unique
 * -Handling Errors and retries
 *
 * **NOTE**: This should be cached By appropriate scope of Ui consumers like: viewModelScope
 */
class ArticlesPagingSource @Inject constructor(
    private val articlesSource: ArticlesDataSource,
    private val contentSource: ContentDataSource
) : PagingSource<DocumentSnapshot, ArticleWithContent>(),
    BasePagingSource<RequestParams> {

    override lateinit var reqParams: RequestParams // filling Request is Consumer responsibility

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, ArticleWithContent> {
        val result = articlesSource.getArticles(
            reqParams.limit,
            reqParams.appreciateNum,
            reqParams.categoriesIds,
            reqParams.withMinutesRead,
            reqParams.mostFollowedPubsId,
            params.key
        )
        return process(
            result,
            params,
            contentSource,
            reqParams.contentLimit
        )
    }
}
