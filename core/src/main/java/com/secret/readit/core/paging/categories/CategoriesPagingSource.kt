/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.paging.categories

import androidx.paging.PagingSource
import com.google.firebase.firestore.DocumentSnapshot
import com.secret.readit.core.data.categories.CategoryDataSource
import com.secret.readit.core.paging.BasePagingSource
import com.secret.readit.core.paging.publisher.process
import com.secret.readit.model.Category
import javax.inject.Inject

/**
 * PagingSource that loads categories in pages
 */
class CategoriesPagingSource @Inject constructor(
    private val categoryDataSource: CategoryDataSource
) : PagingSource<DocumentSnapshot, Category>(), BasePagingSource<RequestParams> {

    override lateinit var reqParams: RequestParams // It is consumer responsibility to fill the request

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, Category> {
        val result = categoryDataSource.getCategories(reqParams.limit, reqParams.ids, params.key)
        return process(result, params)
    }
}
