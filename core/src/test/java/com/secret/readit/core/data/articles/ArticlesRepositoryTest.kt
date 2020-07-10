/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles

import com.secret.readit.core.MainCoroutineRule
import com.secret.readit.core.data.articles.utils.Parser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.google.common.truth.Truth.*
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.secret.readit.core.result.Result

@ExperimentalCoroutinesApi
class ArticlesRepositoryTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    //Object under test
    private lateinit var articlesRepo: ArticlesRepository

    @Before
    fun setUp(){
        /*
        Notice here we used a real object(Parser) in testing, because it is:
        -fast, see: Benchmark results
        -Reliable and well tested, so it cannot fail easily
         */
        articlesRepo = ArticlesRepository(FakeArticlesDataSource(), Parser)
    }

    @Test
    fun dataSourceSuccess_ReturnFormattedArticles() = mainCoroutineRule.runBlockingTest{
        //When getting a result of Articles
        val result = articlesRepo.getNewArticles()

        //Assert it matches our expectations
        assertThat(result).isNotEmpty()
        assertThat(result.size).isEqualTo(1)
        assertThat(result[0].category).isEqualTo(TestData.articles1[0].category)
        assertThat(result[0].publisher).isEqualTo(TestData.articles1[0].publisher)
        //And so on ....
    }

    @Test
    fun dataSourceFails_ReturnEmptyArticles() = mainCoroutineRule.runBlockingTest {
        //GIVEN a data source that fails to get data
        val mockedDataSource = mock<FakeArticlesDataSource> {
            on(it.getArticles()).doReturn(Result.Error(Exception()))
        }

        articlesRepo = ArticlesRepository(mockedDataSource, Parser)

        //When trying to get new results
        val result = articlesRepo.getNewArticles()

        //Assert list is empty
        assertThat(result).isEmpty()
    }
}