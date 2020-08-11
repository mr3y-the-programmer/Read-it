/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.paging

import androidx.paging.PagingSource
import com.google.firebase.firestore.DocumentSnapshot
import com.secret.readit.core.data.articles.ArticlesDataSource
import com.secret.readit.core.data.articles.content.ContentDataSource
import javax.inject.Inject

/**
 * load articles in pages by Using Paging 3 so we can have many benefits like:
 * -in-memory cache support
 * -deduplication of requests, So each request is unique
 * -Handling Errors and retries
 *
 * **NOTE**: This should be cached By appropriate scope of Ui consumers like: viewModelScope
 */
//TODO: handle the lifetime of this Source in dagger
class ArticlesPagingSource @Inject constructor(private val reqParams: RequestParams,
                                               private val articlesSource: ArticlesDataSource,
                                               private val contentSource: ContentDataSource): PagingSource<DocumentSnapshot, ArticleWithContent>(){

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, ArticleWithContent> {
        val result = articlesSource.getArticles(
            reqParams.limit, reqParams.appreciateNum, reqParams.categoriesIds,
            reqParams.withMinutesRead, reqParams.mostFollowedPubsId, params.key
        )
        return process(result, params, contentSource, reqParams.contentLimit)
    }
}

