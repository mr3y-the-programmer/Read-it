/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles.utils

import com.secret.readit.core.MainCoroutineRule
import com.secret.readit.core.data.shared.DummyStorageRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import com.google.common.truth.Truth.assertThat
import com.secret.readit.core.result.Result
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class FormatterTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    // Object Under test
    private lateinit var formatter: Formatter

    @Before
    fun setUp() {
        formatter = Formatter(DummyStorageRepository())
    }

    @Test
    fun formatListOfArticles_allOk_ReturnFormattedArticles() = mainCoroutineRule.runBlockingTest {
        //GIVEN a list of articles to format
        val result = Result.Success(TestData.articles1)

        //When trying to format this list
        val formatResult = formatter.formatArticles(result)

        //assert it matches our expectations
        assertThat(formatResult.size).isEqualTo(1)
        assertThat(formatResult[0].content).isEqualTo(TestData.reverseContent1)
    }

    @Test
    fun formatSingleArticle_allOk_ReturnFormattedArticle() = mainCoroutineRule.runBlockingTest {
        //GIVEN a single article to format
        val result = Result.Success(TestData.article1)

        //When trying to format it
        val formatResult = formatter.formatArticles(result, singleItem = true)

        //assert it matches our expectations
        assertThat(formatResult.size).isEqualTo(1)
        assertThat(formatResult[0].content).isEqualTo(TestData.reverseContent1)
    }

    @Test
    fun formatArticles_failure_ReturnEmptyArticles() = mainCoroutineRule.runBlockingTest {
        //GIVEN a not-Succeeded result
        val result = Result.Loading

        //When trying to format
        val formatResult = formatter.formatArticles(result)

        //assert it matches our expectations
        assertThat(formatResult).isEmpty()
    }

    @Test
    fun deFormatArticles_ReturnDeFormattedArticles() = mainCoroutineRule.runBlockingTest {
        //When trying to deFormat content in order to upload
        val deFormatResult = formatter.deFormatElements(TestData.article1.id, TestData.reverseContent1.elements)

        //Assert the result is deFormatted as expected
        assertThat(deFormatResult).isEqualTo(TestData.content1.elements)
    }
}