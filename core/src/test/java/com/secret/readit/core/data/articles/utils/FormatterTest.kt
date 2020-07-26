/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles.utils

import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.DocumentSnapshot
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.secret.readit.core.MainCoroutineRule
import com.secret.readit.core.data.categories.CategoryRepository
import com.secret.readit.core.data.publisher.PublisherRepository
import com.secret.readit.core.data.shared.DummyStorageRepository
import com.secret.readit.core.result.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
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
        val mockedPubRepo = mock<PublisherRepository> {
            mainCoroutineRule.runBlockingTest { on(it.getPublisherInfo(TestData.publisher1.id)).doReturn(TestData.uiPublisher1) }
        }
        val mockedCategoryRepo = mock<CategoryRepository> {
            mainCoroutineRule.runBlockingTest { on(it.getCategories(TestData.articles1[0].categoryIds)).doReturn(TestData.categories) }
        }
        formatter = Formatter(DummyStorageRepository(), mockedPubRepo, mockedCategoryRepo)
    }

    @Test
    fun formatListOfArticles_allOk_ReturnFormattedArticles() = mainCoroutineRule.runBlockingTest {
        // GIVEN a list of articles to format
        val result = Result.Success(TestData.articles1)

        // When trying to format this list
        val formatResult = formatter.formatArticles(result)

        // assert it matches our expectations
        assertThat(formatResult.size).isEqualTo(1)
        assertThat(formatResult[0].publisher).isEqualTo(TestData.uiPublisher1)
        assertThat(formatResult[0].category).isEqualTo(TestData.categories)
    }

    @Test
    fun formatSingleArticle_allOk_ReturnFormattedArticle() = mainCoroutineRule.runBlockingTest {
        // GIVEN a single article to format
        val result = Result.Success(TestData.article1)

        // When trying to format it
        val formatResult = formatter.formatArticles(result, singleItem = true)

        // assert it matches our expectations
        assertThat(formatResult.size).isEqualTo(1)
        assertThat(formatResult[0].publisher).isEqualTo(TestData.uiPublisher1)
        assertThat(formatResult[0].category).isEqualTo(TestData.categories)
    }

    @Test
    fun formatArticles_failure_ReturnEmptyArticles() = mainCoroutineRule.runBlockingTest {
        // GIVEN a not-Succeeded result
        val result = Result.Loading

        // When trying to format
        val formatResult = formatter.formatArticles(result)

        // assert it matches our expectations
        assertThat(formatResult).isEmpty()
    }

    @Test
    fun deFormatArticles_ReturnDeFormattedArticles() = mainCoroutineRule.runBlockingTest {
        // When trying to deFormat content in order to upload
        val deFormatResult = formatter.deFormatElements(TestData.article1.id, TestData.reverseContent1.elements)

        // Assert the result is deFormatted as expected
        assertThat(deFormatResult).isEqualTo(TestData.content1.elements)
    }

    @Test
    fun formatPubArticles_succeeded_ReturnFormattedArticles() = mainCoroutineRule.runBlockingTest {
        //GIVEN a Success Result
        val result = Result.Success(Pair(TestData.articles1, mock<DocumentSnapshot> {}))

        //When trying to format this result
        val formatResult = formatter.formatPubArticles(result)

        // assert it matches our expectations
        assertThat(formatResult.size).isEqualTo(1)
    }

    @Test
    fun formatPubArticles_failure_ReturnEmpty() = mainCoroutineRule.runBlockingTest {
        //GIVEN a Loading Result
        val result = Result.Loading

        //When trying to format this result
        val formatResult = formatter.formatPubArticles(result)

        // assert we have nothing
        assertThat(formatResult).isEmpty()
    }
}
