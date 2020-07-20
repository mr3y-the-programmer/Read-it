/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.secret.readit.core.MainCoroutineRule
import com.secret.readit.core.data.shared.DummyStorageRepository
import com.secret.readit.core.result.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ArticlesRepositoryTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    // Object under test
    private lateinit var articlesRepo: ArticlesRepository

    @Before
    fun setUp() {
        /*
        Notice here we used a real object(Parser, CustomIDHandler) in testing, because it is:
        -fast, see: Benchmark results
        -Reliable and well tested, so it cannot fail easily
         */
        articlesRepo = ArticlesRepository(FakeArticlesDataSource(), DummyStorageRepository())
    }

    @Test
    fun dataSourceSuccess_ReturnFormattedArticles() = mainCoroutineRule.runBlockingTest {
        // When getting a result of Articles
        val result = articlesRepo.getNewArticles(100)

        // Assert it matches our expectations
        assertThat(result).isNotEmpty()
        assertThat(result.size).isEqualTo(1)
        assertThat(result[0].category).isEqualTo(TestData.articles1[0].category)
        assertThat(result[0].publisher).isEqualTo(TestData.articles1[0].publisher)
        // And so on ....
    }

    @Test
    fun dataSourceFails_ReturnEmptyArticles() = mainCoroutineRule.runBlockingTest {
        // GIVEN a data source that fails to get data
        val mockedDataSource = mock<FakeArticlesDataSource> {
            on(it.getArticles(0, 0, emptyList(), 0, listOf(TestData.publisher1.id))).doReturn(Result.Error(Exception()))
        }

        articlesRepo = articlesRepo.copy(mockedDataSource)

        // When trying to get new results
        val result = articlesRepo.getNewArticles(100)

        // Assert list is empty
        assertThat(result).isEmpty()
    }

    @Test
    fun dataSourceSuccess_ReturnFormattedArticle() = mainCoroutineRule.runBlockingTest {
        // When getting a result of article
        val result = articlesRepo.getArticle(TestData.article1.id)

        // Assert it matches our expectations
        assertThat(result.category).isEqualTo(TestData.article1.category)
        assertThat(result.publisher).isEqualTo(TestData.article1.publisher)
        assertThat(result.comments).isEqualTo(TestData.article1.comments)
        // And so on .....
    }

    @Test
    fun dataSourceFails_ReturnEmptyArticle() = mainCoroutineRule.runBlockingTest {
        // GIVEN a data source that fails to get data
        val mockedDataSource = mock<FakeArticlesDataSource> {
            on(it.getArticle(TestData.article1.id)).doReturn(Result.Error(Exception()))
        }

        articlesRepo = articlesRepo.copy(mockedDataSource)

        // When trying to get new results
        val result = articlesRepo.getArticle(TestData.article1.id)

        // Assert list is empty
        assertThat(result).isEqualTo(TestData.emptyArticle)
    }

    @Test
    fun addArticle2_ReturnTrue() = mainCoroutineRule.runBlockingTest {
        // When trying to add An article(article2)
        val result = articlesRepo.addArticle(TestData.article2)

        // Assert it returns true
        assertThat(result).isTrue()
    }

    @Test
    fun addNonValidArticle_ReturnFalse() = mainCoroutineRule.runBlockingTest {
        // When trying to add An invalid article(emptyArticle)
        val result = articlesRepo.addArticle(TestData.emptyArticle)

        // Assert it returns false
        assertThat(result).isFalse()
    }

    @Test
    fun dataSourceFails_ReturnFalse() = mainCoroutineRule.runBlockingTest {
        // GIVEN a dataSource that fails to add new data
        val mockedDataSource = mock<FakeArticlesDataSource> {
            on(it.addArticle(TestData.article2)).doReturn(Result.Error(Exception()))
        }
        articlesRepo = articlesRepo.copy(mockedDataSource)

        // When trying to add an article
        val result = articlesRepo.addArticle(TestData.article2)

        // Assert it returns false
        assertThat(result).isFalse()
    }

    private fun ArticlesRepository.copy(dataSource: FakeArticlesDataSource): ArticlesRepository {
        return ArticlesRepository(dataSource, DummyStorageRepository())
    }
}
