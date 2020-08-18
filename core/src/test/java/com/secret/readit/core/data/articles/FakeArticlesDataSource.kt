/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles

import com.google.firebase.firestore.DocumentSnapshot
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.secret.readit.core.TestData
import com.secret.readit.core.result.Result
import com.secret.readit.model.Article
import com.secret.readit.model.articleId
import com.secret.readit.model.publisherId

// The class need to be opened, so it can be mocked
// Another Solution by using Mockito2 and make extensions file on Resources
open class FakeArticlesDataSource : ArticlesDataSource {

    private val data1 = mapOf(
        "id" to "4045494-4pub-A Ne",
        "title" to "A New Orleans",
        "publisherID" to "4pubtypoowa0392",
        "numMinutesRead" to 3,
        "timestamp" to 124687893210,
        "numOfAppreciate" to 12,
        "numOfDisagree" to 2,
        "categoryIds" to TestData.categoriesIds
    )

    val mockedSnapshot1 = mock<DocumentSnapshot> {
        on(it.data).doReturn(data1)
    }

    private val data2 = mapOf(
        "id" to "4045494-6pub-What",
        "title" to "What a Wonderful White",
        "publisherID" to "6pubty5456owa0392",
        "numMinutesRead" to 4,
        "timestamp" to 345447893210,
        "numOfAppreciate" to 40,
        "numOfDisagree" to 7,
        "categoryIds" to TestData.categoriesIds.drop(1)
    )

    val mockedSnapshot2 = mock<DocumentSnapshot> {
        on(it.data).doReturn(data2)
    }

    override suspend fun getArticles(
        limit: Int,
        numOfAppreciation: Int,
        containCategories: List<String>,
        numOfMinutesRead: Int,
        pubIds: List<publisherId>,
        prevSnapshot: DocumentSnapshot?
    ): Result<Pair<List<Article>, DocumentSnapshot>> {
        return Result.Success(Pair(TestData.articles1, mockedSnapshot1))
    }

    override suspend fun getArticles(
        limit: Int,
        ids: List<articleId>,
        prevSnapshot: DocumentSnapshot?
    ): Result<Pair<List<Article>, DocumentSnapshot>> {
        return Result.Success(Pair(TestData.articles1, mock {  }))
    }

    override suspend fun getArticle(id: articleId): Result<Article> {
        return Result.Success(TestData.article1)
    }

    override suspend fun getPubArticles(
        info: Pair<publisherId, Long>,
        prevSnapshot: DocumentSnapshot?
    ): Result<Pair<List<Article>, DocumentSnapshot>> {
        return Result.Success(Pair(TestData.articles2, mockedSnapshot2))
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
