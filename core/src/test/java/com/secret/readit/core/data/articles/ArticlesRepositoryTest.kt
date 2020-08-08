/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles

import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.DocumentSnapshot
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.secret.readit.core.MainCoroutineRule
import com.secret.readit.core.SharedMocks
import com.secret.readit.core.TestData
import com.secret.readit.core.data.articles.comments.FakeCommentsDataSource
import com.secret.readit.core.data.articles.content.FakeContentDataSource
import com.secret.readit.core.data.articles.utils.Formatter
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

    private val sharedMocks = SharedMocks(mainCoroutineRule)
    private val formatter = Formatter(FakeContentDataSource(), DummyStorageRepository(), sharedMocks.mockedPubRepo, sharedMocks.mockedCategoryRepo)

    // TODO: refactor
    @Before
    fun setUp() {
        /*
        Notice here we used a real object(Parser, CustomIDHandler) in testing, because it is:
        -fast, see: Benchmark results
        -Reliable and well tested, so it cannot fail easily
         */
        articlesRepo = ArticlesRepository(FakeArticlesDataSource(), FakeCommentsDataSource(), formatter)
    }

    @Test
    fun dataSourceSuccess_ReturnFormattedArticles() = mainCoroutineRule.runBlockingTest {
        // When getting a result of Articles
        val result = articlesRepo.getNewArticles(100)

        // Assert it matches our expectations
        assertThat(result).isNotEmpty()
        assertThat(result.size).isEqualTo(1)
        assertThat(result[0].publisher).isEqualTo(TestData.uiPublisher1)
        assertThat(result[0].category).isEqualTo(TestData.categories)
        assertThat(result[0].initialContent).isEqualTo(TestData.reverseContent1)
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
    fun dataSourceSuccess_ReturnPubArticles() = mainCoroutineRule.runBlockingTest {
        // When getting last-7-days Articles of publisher
        val result = articlesRepo.getNewArticles(0, specificPub = Pair(TestData.publisher2.id, 1594764000))

        // Assert it matches our expectations
        assertThat(articlesRepo.prevSnapshot).isInstanceOf(DocumentSnapshot::class.java)
        assertThat(result).isNotEmpty()
        assertThat(result.size).isEqualTo(1)
        assertThat(result[0].publisher).isEqualTo(TestData.uiPublisher2)
        assertThat(result[0].category).isEqualTo(listOf(TestData.category3))
        assertThat(result[0].initialContent).isEqualTo(TestData.reverseContent1)
        // And so on ....
    }

    @Test
    fun dataSourceFails_ReturnEmptyPubArticles() = mainCoroutineRule.runBlockingTest {
        // GIVEN a data source that fails to get data
        val mockedDataSource = mock<FakeArticlesDataSource> {
            on(it.getPubArticles(Pair(TestData.publisher2.id, 1594764000), null)).doReturn(Result.Error(Exception()))
        }

        articlesRepo = articlesRepo.copy(mockedDataSource)

        // When trying to get new results
        val result = articlesRepo.getNewArticles(0, specificPub = Pair(TestData.publisher2.id, 1594764000))

        // Assert list is empty
        assertThat(result).isEmpty()
        assertThat(articlesRepo.prevSnapshot).isNull() // when it is the first time it should be null
    }
    /* getFullArticles() test Removed because it is no longer contain logic to test */

    @Test
    fun addArticle2_allOk_ReturnTrue() = mainCoroutineRule.runBlockingTest {
        // When trying to add An article(article2)
        val result = articlesRepo.addArticle(TestData.uiArticle2)

        // Assert it returns true
        assertThat(result).isTrue()
    }

    @Test
    fun addNonValidArticle_deFormattingError_ReturnFalse() = mainCoroutineRule.runBlockingTest {
        // When trying to add An invalid article(emptyArticle)
        val result = articlesRepo.addArticle(TestData.emptyUiArticle)

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
        val result = articlesRepo.addArticle(TestData.uiArticle2)

        // Assert it returns false
        assertThat(result).isFalse()
    }

    @Test
    fun appreciate_allOk_ReturnTrue() = mainCoroutineRule.runBlockingTest {
        runUpdateTest({
            articlesRepo.appreciate(
                TestData.uiArticle1
            )
        })
    }

    @Test
    fun disagree_allOk_ReturnTrue() = mainCoroutineRule.runBlockingTest {
        runUpdateTest(
            {
                articlesRepo.disagree(
                    TestData.uiArticle1
                )
            },
            false
        )
    }

    @Test
    fun comment_allOk_ReturnTrue() = mainCoroutineRule.runBlockingTest {
        assertThat(TestData.comments1.size).isEqualTo(2) // Before commenting
        val result = articlesRepo.comment(TestData.uiArticle1.article.id, TestData.newUiComment) // When trying to comment on article

        assertThat(result).isTrue() // assert it succeeded first
        assertThat(TestData.comments1.size).isEqualTo(3) // then check the comment has really been added
    }

    @Test
    fun comment_failure_ReturnFalse() = mainCoroutineRule.runBlockingTest {
        assertThat(TestData.comments1.size).isEqualTo(3) // Before commenting
        val result = articlesRepo.comment(TestData.uiArticle1.article.id, TestData.emptyUiComment) // When trying to comment Invalid comment on article

        assertThat(result).isFalse() // assert it failed
        assertThat(TestData.comments1.size).isEqualTo(3) // then check the comment hasn't been published
    }

    @Test
    fun reply_allOk_ReturnTrue() = mainCoroutineRule.runBlockingTest {
        assertThat(TestData.newComment.repliesIds.size).isEqualTo(0) // Before replying
        val result = articlesRepo.reply(TestData.uiArticle1.article.id, TestData.newUiReply, TestData.newUiComment) // When trying to reply to comment

        assertThat(result).isTrue() // assert it succeeded first
        assertThat(TestData.newComment.repliesIds.size).isEqualTo(1) // then check the reply has really been added
    }

    @Test
    fun reply_failure_ReturnFalse() = mainCoroutineRule.runBlockingTest {
        assertThat(TestData.newComment.repliesIds.size).isEqualTo(0) // Before replying
        val result = articlesRepo.reply(TestData.uiArticle1.article.id, TestData.emptyUiComment, TestData.newUiComment) // When trying to reply with Invalid reply on article

        assertThat(result).isFalse() // assert it failed
        assertThat(TestData.newComment.repliesIds.size).isEqualTo(0) // then check the reply hasn't been published
    }

    private suspend fun runUpdateTest(funUnderTest: suspend ArticlesRepository.() -> Boolean, agree: Boolean = true) {
        val result = articlesRepo.funUnderTest()
        val field = if (agree) TestData.uiArticle1.article.numOfAppreciate else TestData.uiArticle1.article.numOfDisagree

        // Assert result is true and field updated
        assertThat(result).isTrue()
        assertThat(field).isGreaterThan(0)
    }

    private fun ArticlesRepository.copy(dataSource: FakeArticlesDataSource): ArticlesRepository {
        return ArticlesRepository(dataSource, FakeCommentsDataSource(), formatter)
    }
}
