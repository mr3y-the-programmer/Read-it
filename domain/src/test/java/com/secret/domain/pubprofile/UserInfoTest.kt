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
import com.secret.readit.core.data.categories.CategoryRepository
import com.secret.readit.core.data.publisher.PublisherRepository
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
    private val categoryUseCase: FlowUseCase<Unit, PagingData<Category>>
) {

    companion object {
        val mainCoroutineRule = MainCoroutineRule()
        private val mockedPubRepo = mock<PublisherRepository> {
            mainCoroutineRule.runBlockingTest {
                on(it.getFollowingPubsList(TestData.publisher1.followedPublishersIds)).doReturn(
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
        // Objects under the test
        @JvmStatic
        @Parameterized.Parameters
        fun testUseCases() = listOf(
            arrayOf(
                GetFollowingUseCase(FakeCurrentUser(), mockedPubRepo),
                GetCategoriesUseCase(
                    FakeCurrentUser(), mockedCategoryRepo
                )
            ),
            arrayOf(
                GetFollowingUseCase(FakeCurrentUser(), mockedPubRepo),
                GetCategoriesUseCase(
                    FakeCurrentUser(), mockedCategoryRepo
                )
            )
        )
    }

    @get:Rule
    val coroutineRule = mainCoroutineRule

    @Test
    fun allOk_ReturnExpectedResult() = mainCoroutineRule.runBlockingTest {
        val followingList = mutableListOf<UiPublisher>()
        val categoriesList = mutableListOf<Category>()
        // When trying to collect the result
        followingUseCase(Unit).collect { it.map { pub -> followingList.add(pub) } }
        categoryUseCase(Unit).collect { it.map { category -> categoriesList.add(category) } }

        // Assert it all goes as intended
        assertThat(followingList).isNotEmpty()
        assertThat(categoriesList).isNotEmpty()
        assertThat(followingList).isEqualTo(listOf(TestData.uiPublisher2))
        assertThat(categoriesList).isEqualTo(TestData.categories)
    }
}
