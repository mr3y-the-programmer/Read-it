/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles

import com.google.firebase.firestore.DocumentSnapshot
import com.nhaarman.mockitokotlin2.mock
import com.secret.readit.core.result.Result
import com.secret.readit.model.Article
import com.secret.readit.model.articleId
import com.secret.readit.model.publisherId

// The class need to be opened, so it can be mocked
// Another Solution by using Mockito2 and make extensions file on Resources
open class FakeArticlesDataSource : ArticlesDataSource {
    override suspend fun getArticles(
        limit: Int,
        numOfAppreciation: Int,
        containCategories: List<String>,
        numOfMinutesRead: Int,
        pubIds: List<publisherId>
    ): Result<List<Article>> {
        return Result.Success(TestData.articles1)
    }

    override suspend fun getArticle(id: articleId): Result<Article> {
        return Result.Success(TestData.article1)
    }

    override suspend fun getPubArticles(info: Pair<publisherId, Long>, prevSnapshot: DocumentSnapshot?): Result<Pair<List<Article>, DocumentSnapshot>> {
        val mockedSnapshot = mock<DocumentSnapshot> {/*no-op*/}
        return Result.Success(Pair(TestData.articles2, mockedSnapshot))
    }

    override suspend fun addArticle(article: Article): Result<Boolean> {
        return Result.Success(true)
    }

    override suspend fun incrementAppreciation(id: articleId): Result<Boolean> {
        val newArticle = TestData.article1.copy(numOfAppreciate = TestData.article1.numOfAppreciate.plus(1))
        TestData.uiArticle1 = TestData.uiArticle1.copy(article = newArticle)
        return Result.Success(true)
    }

    override suspend fun incrementDisagree(id: articleId): Result<Boolean> {
        val newArticle = TestData.article1.copy(numOfDisagree = TestData.article1.numOfDisagree.plus(1))
        TestData.uiArticle1 = TestData.uiArticle1.copy(article = newArticle)
        return Result.Success(true)
    }
}
