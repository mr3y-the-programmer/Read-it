/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles.utils

import com.google.common.truth.Truth.assertThat
import com.secret.readit.core.MainCoroutineRule
import com.secret.readit.core.SharedMocks
import com.secret.readit.core.TestData
import com.secret.readit.core.data.articles.content.FakeContentDataSource
import com.secret.readit.core.data.shared.DummyStorageRepository
import com.secret.readit.core.result.Result
import com.secret.readit.core.uimodels.UiComment
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
        formatter = Formatter(DummyStorageRepository(), sharedMocks.mockedPubRepo, sharedMocks.mockedCategoryRepo)
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun formatContent_allOk() = mainCoroutineRule.runBlockingTest {
        // When trying to format some content
        val formatResult = formatter.formatContent(TestData.fullArticleContent)

        // assert it matches our expectations
        assertThat(formatResult).isEqualTo(TestData.reverseFullArticleContent.elements)
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
    fun formatComments_succeeded_ReturnFormattedComments() = mainCoroutineRule.runBlockingTest {
        // GIVEN a Success result
        val result = Result.Success(TestData.comments1)

        // When trying to format it
        val comments = formatter.formatComments(result)

        // assert it matches our expectations
        assertThat(comments.size).isEqualTo(2)
        assertThat(comments[0].pub).isEqualTo(TestData.uiPublisher1)
        assertThat(comments[1].replies).isEmpty() // For now we haven't loaded replies yet, So it should be empty for now

        // When trying to format result
        val commentWithReplies = formatter.formatReplies(Result.Success(TestData.comment2Replies), comments[1])

        assertThat(commentWithReplies.replies).isNotEmpty() // Assert now we have replies tied to comment
        assertThat(commentWithReplies.replies[0]).isEqualTo(UiComment(TestData.comment0, TestData.uiPublisher2))
    }

    @Test
    fun formatComments_failure_ReturnEmpty() = runFailureTest { formatter.formatComments(Result.Loading) }

    @Test
    fun deFormatComment_succeeded_ReturnFirestoreComment() {
        // When trying to deFormat valid uiComment
        val comment = formatter.deFormatComment(TestData.deFormatTestComment)

        // assert it matches our expectations
        assertThat(comment?.id).isNotEmpty()
        assertThat(comment?.publisherID).isEqualTo(TestData.publisher1.id)
        assertThat(comment?.timestamp).isGreaterThan(1000)
        assertThat(comment?.repliesIds).isEmpty()
        assertThat(comment?.text).isEqualTo(TestData.comment4.text)
    }

    private fun runFailureTest(funUnderTest: suspend Formatter.() -> List<*>) = mainCoroutineRule.runBlockingTest {
        // When trying to format this failure result
        val formatResult = formatter.funUnderTest()

        // assert we have nothing
        assertThat(formatResult).isEmpty()
    }
}
