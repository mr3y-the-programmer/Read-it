/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles

import com.secret.readit.core.result.Result
import com.secret.readit.model.Article
import com.secret.readit.model.articleId

// The class need to be opened, so it can be mocked
// Another Solution by using Mockito2 and make extensions file on Resources
open class FakeArticlesDataSource : ArticlesDataSource {
    override suspend fun getArticles(): Result<List<Article>> {
        return Result.Success(TestData.articles1)
    }

    override suspend fun getArticle(id: articleId): Result<Article> {
        return Result.Success(TestData.article1)
    }

    override suspend fun addArticle(article: Article): Result<Boolean> {
        if (article.id == TestData.article2.id) {
            return Result.Success(true)
        }
        return Result.Success(false)
    }
}
