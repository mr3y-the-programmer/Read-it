/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.paging.articles

import androidx.paging.PagingSource
import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.DocumentSnapshot
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.secret.readit.core.MainCoroutineRule
import com.secret.readit.core.TestData
import com.secret.readit.core.data.articles.FakeArticlesDataSource
import com.secret.readit.core.data.articles.content.FakeContentDataSource
import com.secret.readit.core.paging.BasePagingSource
import com.secret.readit.core.result.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Parametrized test for [ArticlesPagingSource], [PubArticlesPagingSource]
 */
@ExperimentalCoroutinesApi
@RunWith(Parameterized::class)
class ArticlesSourcesTest(
    private var firstSource: PagingSource<DocumentSnapshot, ArticleWithContent>,
    private var secondSource: PagingSource<DocumentSnapshot, ArticleWithContent>
) {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private var reqParams =
        Companion.reqParams
    private var mockedArticlesSource =
        Companion.mockedArticlesSource

    companion object {
        private var reqParams =
            RequestParams(
                limit = 50,
                appreciateNum = 70,
                categoriesIds = TestData.categoriesIds,
                mostFollowedPubsId = emptyList(),
                withMinutesRead = 3,
                specificPub = Pair("2pub", 14525478887),
                articleIds = emptyList(),
                contentLimit = 5
            )

        private var mockedArticlesSource = FakeArticlesDataSource()

        @Parameterized.Parameters
        @JvmStatic
        fun objectsUnderTest() = listOf( // Run test 2 times
            arrayOf(
                ArticlesPagingSource(
                    mockedArticlesSource,
                    FakeContentDataSource()
                ).withParams<ArticleWithContent>(reqParams),
                PubArticlesPagingSource(
                    mockedArticlesSource,
                    FakeContentDataSource()
                ).withParams(reqParams)
            ),

            arrayOf(
                ArticlesPagingSource(
                    mockedArticlesSource,
                    FakeContentDataSource()
                ).withParams(reqParams),
                PubArticlesPagingSource(
                    mockedArticlesSource,
                    FakeContentDataSource()
                ).withParams(reqParams)
            )
        )
    }
    // TODO: Refactor boilerplate

    // When it is the first time to load
    @Test
    fun `noInitialKey getArticles allOk ReturnLoadResultOfPage`() = mainCoroutineRule.runBlockingTest {
        val homeFeedResult = firstSource.load(mockLoadParams(null))
        val pubArticlesResult = secondSource.load(mockLoadParams(null))
        assertOk(homeFeedResult)
        assertOk(pubArticlesResult, true)
    }

    // When it fails whether it first time or not
    @Test
    @Suppress("UNCHECKED_CAST")
    fun `getArticles failure ReturnLoadResultOfError`() = mainCoroutineRule.runBlockingTest {
        firstSource = (failArticlesSource() as BasePagingSource<RequestParams>).withParams(reqParams)
        secondSource = (failPubArticlesSource() as BasePagingSource<RequestParams>).withParams(reqParams)
        val homeFeedResult = firstSource.load(mockLoadParams(mockedArticlesSource.mockedSnapshot1))
        val pubArticlesResult = secondSource.load(mockLoadParams(mockedArticlesSource.mockedSnapshot2))
        assertThat(homeFeedResult).isInstanceOf(PagingSource.LoadResult.Error::class.java)
        assertThat(pubArticlesResult).isInstanceOf(PagingSource.LoadResult.Error::class.java)
    }

    private fun assertOk(result: PagingSource.LoadResult<DocumentSnapshot, ArticleWithContent>, isPubArticles: Boolean = false) {
        assertThat(result).isInstanceOf(PagingSource.LoadResult.Page::class.java)
        val page = result as PagingSource.LoadResult.Page
        val snapshot = if (isPubArticles) FakeArticlesDataSource().mockedSnapshot2.data else FakeArticlesDataSource().mockedSnapshot1.data
        val article = if (isPubArticles) TestData.article2 else TestData.article1

        assertThat(page.nextKey?.data).isEqualTo(snapshot)
        assertThat(page.prevKey).isNull()
        assertThat(page.data).isEqualTo(listOf(Pair(article, TestData.content1)))
    }

    private fun mockLoadParams(value: DocumentSnapshot?): PagingSource.LoadParams<DocumentSnapshot> = mock { on(it.key).doReturn(value) }

    private suspend fun failArticlesSource(): PagingSource<DocumentSnapshot, ArticleWithContent> {
        mockedArticlesSource = mock {
            on(
                it.getArticles(
                    reqParams.limit, reqParams.appreciateNum,
                    reqParams.categoriesIds, reqParams.withMinutesRead, emptyList(), null
                )
            ).doReturn(Result.Error(Exception()))
        }
        return ArticlesPagingSource(
            mockedArticlesSource,
            FakeContentDataSource()
        )
    }

    private suspend fun failPubArticlesSource(): PagingSource<DocumentSnapshot, ArticleWithContent> {
        mockedArticlesSource = mock {
            on(it.getPubArticles(reqParams.specificPub, null)).doReturn(Result.Error(Exception()))
        }
        return PubArticlesPagingSource(
            mockedArticlesSource,
            FakeContentDataSource()
        )
    }
}
