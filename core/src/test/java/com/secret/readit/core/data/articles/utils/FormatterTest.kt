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
import com.secret.readit.core.SharedMocks
import com.secret.readit.core.data.articles.content.FakeContentDataSource
import com.secret.readit.core.data.shared.DummyStorageRepository
import com.secret.readit.core.result.Result
import com.secret.readit.model.Element
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

    val sharedMocks = SharedMocks(mainCoroutineRule)
    @Before
    fun setUp() {
        formatter = Formatter(FakeContentDataSource(), DummyStorageRepository(), sharedMocks.mockedPubRepo, sharedMocks.mockedCategoryRepo)
    }

    @Test
    fun formatListOfArticles_allOk_ReturnFormattedArticles() = mainCoroutineRule.runBlockingTest {
        // GIVEN a list of articles to format
        val result = Result.Success(TestData.articles1)

        // When trying to format this list
        val formatResult = formatter.formatArticles(result, 5)

        // assert it matches our expectations
        assertThat(formatResult.size).isEqualTo(1)
        assertThat(formatResult[0].publisher).isEqualTo(TestData.uiPublisher1)
        assertThat(formatResult[0].category).isEqualTo(TestData.categories)
        assertThat(formatResult[0].initialContent).isEqualTo(TestData.reverseContent1)
        assertThat(formatResult[0].fullContent).isEqualTo(TestData.reverseContent1)
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun formatFullArticle_allOk_ReturnFullContent() = mainCoroutineRule.runBlockingTest {
        val mockedContentSource = mock<FakeContentDataSource> {
            on(it.getContent(TestData.uiArticle1.article.id, 0)).doReturn(Result.Success(TestData.fullArticleContent.elements as List<Element>))
        }
        formatter = Formatter(mockedContentSource, DummyStorageRepository(), sharedMocks.mockedPubRepo, sharedMocks.mockedCategoryRepo)
        // When trying to format partial article
        val formatResult = formatter.formatFullArticle(TestData.uiArticle1)

        // assert it matches our expectations
        assertThat(formatResult.publisher).isEqualTo(TestData.uiPublisher1)
        assertThat(formatResult.category).isEqualTo(TestData.categories)
        assertThat(formatResult.initialContent).isEqualTo(TestData.uiArticle1.initialContent)
        assertThat(formatResult.fullContent).isEqualTo(TestData.reverseFullArticleContent)
    }

    @Test
    fun formatArticles_failure_ReturnEmptyArticles() = mainCoroutineRule.runBlockingTest {
        // GIVEN a not-Succeeded result
        val result = Result.Loading

        // When trying to format
        val formatResult = formatter.formatArticles(result, 5)

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
        val formatResult = formatter.formatPubArticles(result, 5)

        // assert it matches our expectations
        assertThat(formatResult.size).isEqualTo(1)
        assertThat(formatResult[0].publisher).isEqualTo(TestData.uiPublisher1)
        assertThat(formatResult[0].category).isEqualTo(TestData.categories)
        assertThat(formatResult[0].initialContent).isEqualTo(TestData.reverseContent1)
        assertThat(formatResult[0].fullContent).isEqualTo(TestData.reverseContent1)
    }

    @Test
    fun formatPubArticles_failure_ReturnEmpty() = mainCoroutineRule.runBlockingTest {
        //GIVEN a Loading Result
        val result = Result.Loading

        //When trying to format this result
        val formatResult = formatter.formatPubArticles(result, 5)

        // assert we have nothing
        assertThat(formatResult).isEmpty()
    }
}
