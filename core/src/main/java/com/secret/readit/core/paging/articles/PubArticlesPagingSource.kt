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
import com.secret.readit.core.paging.*
import com.secret.readit.core.paging.process
import javax.inject.Inject

/**
 * Identical to [ArticlesPagingSource] but For articles with specific publisher
 * **NOTE**: This should be cached By appropriate scope of Ui consumers like: viewModelScope
 */
//TODO: handle the lifetime of this Source in dagger
class PubArticlesPagingSource @Inject constructor(
    private val articlesSource: ArticlesDataSource,
    private val contentSource: ContentDataSource
): PagingSource<DocumentSnapshot, ArticleWithContent>(),
    BasePagingSource {

    override var reqParams: RequestParams =
        emptyReq() //It is empty for now, filling Request is Consumer responsibility

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, ArticleWithContent> {
        val result = articlesSource.getPubArticles(reqParams.specificPub, params.key)
        return process(
            result,
            params,
            contentSource,
            reqParams.contentLimit
        )
    }

    companion object {
        //Create A new Instance of PagingSource
        fun create(articlesSource: ArticlesDataSource, contentSource: ContentDataSource): PubArticlesPagingSource {
            return PubArticlesPagingSource(
                articlesSource = articlesSource,
                contentSource = contentSource
            )
        }
    }
}