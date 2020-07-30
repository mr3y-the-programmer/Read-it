/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.domain.pubprofile

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.secret.readit.core.MainCoroutineRule
import com.secret.readit.core.data.articles.ArticlesRepository
import com.secret.readit.core.data.publisher.PubImportantInfo
import com.secret.readit.core.data.publisher.PublisherRepository
import com.secret.readit.core.uimodels.UiArticle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@ExperimentalCoroutinesApi
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class PublishedArticlesSinceTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    // Object Under test
    private lateinit var pubArticlesSince: PublishedArticlesSince

    private val testPubInfo = PubImportantInfo(TestData.publisher1.name, TestData.publisher1.emailAddress, TestData.publisher1.memberSince)

    @Before
    fun setUp() {
        val period = ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).minusDays(7).toEpochSecond()
        val mockedArticlesRepo = mock<ArticlesRepository> {
            mainCoroutineRule.runBlockingTest {
                on(it.getPubArticlesSince(TestData.publisher1.id, period))
                    .doReturn(TestData.uiArticles)
            }
        }

        val mockedPubRepo = mock<PublisherRepository> { mainCoroutineRule.runBlockingTest { on(it.getPublisherId(testPubInfo)).doReturn(TestData.publisher1.id) } }

        pubArticlesSince = PublishedArticlesSince(mockedPubRepo, mockedArticlesRepo)
    }

    @Test
    fun allOk_getPublishedSince() = mainCoroutineRule.runBlockingTest {
        val returnedArticles = mutableListOf<UiArticle>()
        // When trying to get the published articles last seven days
        pubArticlesSince(Pair(testPubInfo, Since.LAST_7_DAYS)).collect { returnedArticles.add(it) }

        // Assert empty articles dropped
        assertThat(returnedArticles).isEqualTo(TestData.uiArticles.dropLast(1))
    }

    @Test
    fun failedToGetId_ReturnEmpty() = mainCoroutineRule.runBlockingTest {
        // GIVEN invalid pubId
        val mockedPubRepo = mock<PublisherRepository> { on(it.getPublisherId(testPubInfo)).doReturn(null) }
        val period = ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).minusDays(7).toEpochSecond()
        val mockedArticlesRepo = mock<ArticlesRepository> { on(it.getPubArticlesSince(TestData.publisher1.id, period)).doReturn(TestData.uiArticles) }

        pubArticlesSince = PublishedArticlesSince(mockedPubRepo, mockedArticlesRepo)

        // When trying to get Articles of this publisher
        val returnArticles = mutableListOf<UiArticle>()
        pubArticlesSince(Pair(testPubInfo, Since.LAST_7_DAYS)).toCollection(returnArticles)

        // Assert the list is empty since there's exception happened
        assertThat(returnArticles).isEmpty()
    }
}
