/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.categories

import com.secret.readit.core.data.utils.CustomIDHandler
import com.secret.readit.core.result.Result
import com.secret.readit.core.result.succeeded
import com.secret.readit.model.Article
import com.secret.readit.model.Category
import javax.inject.Inject

/**
 * Any consumers should interact with this Repo not with dataSource directly
 *
 * Rule: -forward actions when needed to dataSource
 *       -normalize data(with help of some utils) to consumers in expected format
 */
class CategoryRepository @Inject constructor(private val categoryDataSource: CategoryDataSource,
                                             private val idHandler: CustomIDHandler = CustomIDHandler()
) {

    /**
     * Return Categories from data source or empty if dataSource failed
     */
    //TODO: normalize Int color to Color
    suspend fun getAllCategories(): List<Category>{
        val result = categoryDataSource.getCategories()
        return getCategories(result)
    }

    /**
     * Return Categories associated with each article, or empty list if any failure happens
     */
    suspend fun getArticleCategories(article: Article): List<Category> {
        var id = ""
        try {
            id = idHandler.getID(article)
        } catch (ex: IllegalArgumentException) {
            return emptyList()
        }
        val result = categoryDataSource.getArticleCategories(id)
        return getCategories(result)
    }

    private fun getCategories(result: Result<List<Category>>): List<Category>{
        val formattedCategories = mutableListOf<Category>()
        if (result != null && result.succeeded){
            val categories = (result as Result.Success).data
            for (category in categories) formattedCategories += category
        }
        return formattedCategories
    }
}