/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.categories

import com.google.firebase.firestore.DocumentSnapshot
import com.nhaarman.mockitokotlin2.mock
import com.secret.readit.core.TestData
import com.secret.readit.core.result.Result
import com.secret.readit.model.Category
import com.secret.readit.model.articleId

open class FakeCategoryDataSource : CategoryDataSource {
    override suspend fun getCategories(
        limit: Int,
        ids: List<String>,
        prevSnapshot: DocumentSnapshot?
    ): Result<Pair<List<Category>, DocumentSnapshot>> {
        return Result.Success(Pair(TestData.categories, mock { }))
    }

    override suspend fun getArticleCategories(id: articleId): Result<List<Category>> {
        return Result.Success(TestData.articleCategories)
    }
}
