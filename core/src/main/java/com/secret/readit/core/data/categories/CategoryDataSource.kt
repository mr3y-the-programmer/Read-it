/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.categories

import com.secret.readit.core.result.Result
import com.secret.readit.model.Category
import com.secret.readit.model.articleId

/**
 * Blueprint for basic operations on categories
 */
interface CategoryDataSource {

    /**
     * the main fun to get categories stored in Categories collection,
     * it takes additional parameters like [ids] to customize query,
     * if all parameters empty it will return the whole list of categories
     */
    suspend fun getCategories(ids: List<String>): Result<List<Category>>

    /**
     * get categories of specified [articleId]
     */
    suspend fun getArticleCategories(id: articleId): Result<List<Category>>
}
