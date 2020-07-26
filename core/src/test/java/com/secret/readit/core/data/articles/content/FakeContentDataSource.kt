/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles.content

import com.secret.readit.core.result.Result
import com.secret.readit.model.Element
import com.secret.readit.model.articleId

class FakeContentDataSource: ContentDataSource {
    @Suppress("UNCHECKED_CAST")
    override suspend fun getContent(id: articleId, limit: Int): Result<List<Element>> {
        return Result.Success(TestData.content1.elements as List<Element>)
    }

    override suspend fun addContent(id: articleId, elements: List<Element>): Result<Boolean> {
        return Result.Success(true)
    }
}