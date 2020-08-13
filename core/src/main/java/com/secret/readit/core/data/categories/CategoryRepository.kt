/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.categories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.secret.readit.core.paging.BasePagingSource
import com.secret.readit.core.paging.categories.RequestParams
import com.secret.readit.core.result.Result
import com.secret.readit.core.result.succeeded
import com.secret.readit.model.Category
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Any consumers should interact with this Repo not with dataSource directly
 *
 * Rule: -forward actions when needed to dataSource(By Using PagingSource)
 *       -normalize data(with help of some utils) to consumers in expected format
 */
@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDataSource: CategoryDataSource,
    private val categoryPagingSource: BasePagingSource<RequestParams>) {

    /**
     * Return Categories from data source or empty if dataSource failed
     * **NOTE**: This Fun is intended to be used by internal consumers only.
     * For External Usage, see [getCategories(limit,...etc)]
     */
    // TODO: normalize Int color to Color
    internal suspend fun getCategories(ids: List<String>): List<Category> {
        val result = categoryDataSource.getCategories(999, ids, null)
        return if (result != null && result.succeeded) (result as Result.Success).data.first else emptyList()
    }

    /**
     * Return Categories associated with each article, or empty list if any failure happens
     */
    //TODO: this will be removed later (UNNEEDED)
    /*suspend fun getArticleCategories(article: UiArticle): List<Category> {
        val id = try {
            idHandler.getID(article.article)
        } catch (ex: IllegalArgumentException) {
            return emptyList()
        }
        val result = categoryDataSource.getArticleCategories(id)
        return getCategories(result)
    }*/

    fun getCategories(limit: Int, ids: List<String> = emptyList()): Flow<PagingData<Category>> {
        val params = RequestParams(limit, ids)
        return Pager(
            config = PagingConfig(limit),
            pagingSourceFactory = { categoryPagingSource.withParams<Category>(params)}
        ).flow
    }
}
