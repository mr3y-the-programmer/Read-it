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
import com.secret.readit.core.data.categories.CategoryRepository
import com.secret.readit.core.data.publisher.PublisherRepository
import com.secret.readit.core.domain.FlowUseCase
import com.secret.readit.core.uimodels.UiPublisher
import com.secret.readit.model.Category
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
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
    private val followingUseCase: FlowUseCase<Unit, UiPublisher>,
    private val categoryUseCase: FlowUseCase<Unit, Category>
) {

    companion object {
        val mainCoroutineRule = MainCoroutineRule()
        private val mockedPubRepo = mock<PublisherRepository> {
            mainCoroutineRule.runBlockingTest { on(it.getFollowingPubsList(TestData.publisher1.followedPublishersIds)).doReturn(listOf(TestData.uiPublisher2)) }
        }
        private val mockedCategoryRepo = mock<CategoryRepository> {
            mainCoroutineRule.runBlockingTest { on(it.getCategories(TestData.publisher1.followedCategoriesIds)).doReturn(TestData.categories) }
        }
        // Objects under the test
        @JvmStatic
        @Parameterized.Parameters
        fun testUseCases() = listOf(
            arrayOf(GetFollowingUseCase(FakeCurrentUser(), mockedPubRepo), GetCategoriesUseCase(FakeCurrentUser(), mockedCategoryRepo)),
            arrayOf(GetFollowingUseCase(FakeCurrentUser(), mockedPubRepo), GetCategoriesUseCase(FakeCurrentUser(), mockedCategoryRepo))
        )
    }

    @get:Rule
    val coroutineRule = mainCoroutineRule

    @Test
    fun allOk_ReturnExpectedResult() = mainCoroutineRule.runBlockingTest {
        val followingList = mutableListOf<UiPublisher>()
        val categoriesList = mutableListOf<Category>()
        // When trying to collect the result
        followingUseCase(Unit).collect { followingList.add(it) }
        categoryUseCase(Unit).collect { categoriesList.add(it) }

        // Assert it all goes as intended
        assertThat(followingList).isNotEmpty()
        assertThat(categoriesList).isNotEmpty()
        assertThat(followingList).isEqualTo(listOf(TestData.uiPublisher2))
        assertThat(categoriesList).isEqualTo(TestData.categories)
    }
}
