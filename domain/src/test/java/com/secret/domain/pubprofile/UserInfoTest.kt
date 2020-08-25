/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.domain.pubprofile

import androidx.paging.PagingData
import androidx.paging.map
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.secret.domain.FlowUseCase
import com.secret.readit.core.MainCoroutineRule
import com.secret.readit.core.TestData
import com.secret.readit.core.data.articles.ArticlesRepository
import com.secret.readit.core.data.categories.CategoryRepository
import com.secret.readit.core.data.publisher.PublisherRepository
import com.secret.readit.core.uimodels.UiArticle
import com.secret.readit.core.uimodels.UiPublisher
import com.secret.readit.model.Category
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Parametrized test for [GetFollowingUseCase], [GetCategoriesUseCase]
 * which test the user identity related Info
 */
@RunWith(Parameterized::class)
@ExperimentalCoroutinesApi
class UserInfoTest(
    private val followingUseCase: FlowUseCase<Unit, PagingData<UiPublisher>>,
    private val categoryUseCase: FlowUseCase<Unit, PagingData<Category>>,
    private val bookmarkedUseCase: FlowUseCase<Unit, PagingData<UiArticle>>
) {

    companion object {
        val mainCoroutineRule = MainCoroutineRule()
        private val mockedPubRepo = mock<PublisherRepository> {
            mainCoroutineRule.runBlockingTest {
                on(it.getPubs(TestData.publisher1.followedPublishersIds)).doReturn(
                    flowOf(PagingData.from(listOf(TestData.uiPublisher2)))
                )
            }
        }
        private val mockedCategoryRepo = mock<CategoryRepository> {
            mainCoroutineRule.runBlockingTest {
                on(it.getCategories(50, TestData.publisher1.followedCategoriesIds)).doReturn(
                    flowOf(PagingData.from(TestData.categories))
                )
            }
        }
        private val mockedArticlesRepo = mock<ArticlesRepository> {
            mainCoroutineRule.runBlockingTest {
                on(it.getArticlesWithIds(TestData.uiArticles.map { it -> it.article.id })).doReturn(
                    flowOf(PagingData.from(TestData.uiArticles))
                )
            }
        }
        // Objects under the test
        @JvmStatic
        @Parameterized.Parameters
        fun testUseCases() = listOf(
            arrayOf(
                GetFollowingUseCase(FakeCurrentUser(), mockedPubRepo),
                GetCategoriesUseCase(FakeCurrentUser(), mockedCategoryRepo, DummyRemoteConfig()),
                GetBookmarks(FakeCurrentUser(), mockedArticlesRepo)
            ),
            arrayOf(
                GetFollowingUseCase(FakeCurrentUser(), mockedPubRepo),
                GetCategoriesUseCase(FakeCurrentUser(), mockedCategoryRepo, DummyRemoteConfig()),
                GetBookmarks(FakeCurrentUser(), mockedArticlesRepo)
            )
        )
    }

    @get:Rule
    val coroutineRule = mainCoroutineRule

    @Test
    fun allOk_ReturnExpectedResult() = mainCoroutineRule.runBlockingTest {
        val followingList = mutableListOf<UiPublisher>()
        val categoriesList = mutableListOf<Category>()
        val bookmarkedList = mutableListOf<UiArticle>()
        // When trying to collect the result
        followingUseCase(Unit).collect { it.map { pub -> followingList.add(pub) } }
        categoryUseCase(Unit).collect { it.map { category -> categoriesList.add(category) } }
        bookmarkedUseCase(Unit).collect { it.map { article -> bookmarkedList.add(article) } }

        // Assert it all goes as intended
        assertThat(followingList).isNotEmpty()
        assertThat(categoriesList).isNotEmpty()
        assertThat(bookmarkedList).isNotEmpty()
        assertThat(followingList).isEqualTo(listOf(TestData.uiPublisher2))
        assertThat(categoriesList).isEqualTo(TestData.categories)
        assertThat(bookmarkedList).isEqualTo(TestData.uiArticles.dropLast(1)) //Empty filtered
    }
}
