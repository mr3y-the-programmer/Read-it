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
import com.secret.readit.core.uimodels.UiComment
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
        assertThat(formatResult.category).isEqualTo(TestData.articleCategories)
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
    fun deFormatUiArticles_allOk_ReturnDeFormattedArticles() = mainCoroutineRule.runBlockingTest {
        // When trying to deFormat valid article
        val deFormatResult = formatter.deFormatArticle(TestData.uiArticleToBeDeFormatted)
        val article = deFormatResult!!.first
        val elements = deFormatResult.second

        // Assert the result is deFormatted as expected
        assertThat(elements).isEqualTo(TestData.fullArticleContent.elements)
        println(article.numMinutesRead)
        assertThat(article.numMinutesRead).isAtLeast(0)
        assertThat(article.numOfAppreciate).isEqualTo(0)
        assertThat(article.numOfDisagree).isEqualTo(0)
        assertThat(article.categoryIds).isEqualTo(listOf(TestData.category1.id, TestData.category2.id))
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

    @Test
    fun formatComments_succeeded_ReturnFormattedComments() = mainCoroutineRule.runBlockingTest {
        //GIVEN a Success result
        val result = Result.Success(TestData.comments1)

        //When trying to format it
        val comments = formatter.formatComments(result)

        //assert it matches our expectations
        assertThat(comments.size).isEqualTo(2)
        assertThat(comments[0].pub).isEqualTo(TestData.uiPublisher1)
        assertThat(comments[1].replies).isEmpty() //For now we haven't loaded replies yet, So it should be empty for now

        //When trying to format result
        val commentWithReplies = formatter.formatReplies(Result.Success(TestData.comment2Replies), comments[1])

        assertThat(commentWithReplies.replies).isNotEmpty() //Assert now we have replies tied to comment
        assertThat(commentWithReplies.replies[0]).isEqualTo(UiComment(TestData.comment0, TestData.uiPublisher2))
    }

    @Test
    fun formatComments_failure_ReturnEmpty() = mainCoroutineRule.runBlockingTest {
        //GIVEN a Loading Result
        val result = Result.Loading

        //When trying to format this result
        val formatResult = formatter.formatComments(result)

        // assert we have nothing
        assertThat(formatResult).isEmpty()
    }
}
