/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.categories

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.secret.readit.core.MainCoroutineRule
import com.secret.readit.core.result.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CategoryRepositoryTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    // Object under test
    private lateinit var categoryRepo: CategoryRepository

    @Before
    fun setUp() {
        categoryRepo = CategoryRepository(FakeCategoryDataSource())
    }

    @Test
    fun dataSourceSuccess_ReturnAllCategories() = mainCoroutineRule.runBlockingTest {
        // When trying to get a Successful result
        val result = categoryRepo.getCategories(TestData.categoriesIds)

        val expected = TestData.categories

        // Assert it equals our expectations
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun dataSourceFails_ReturnEmptyResult() = mainCoroutineRule.runBlockingTest {
        // GIVEN data Source that fails to get new result
        val mockedDataSource = mock<FakeCategoryDataSource> {
            on(it.getCategories(emptyList())).doReturn(Result.Error(Exception()))
        }

        categoryRepo = categoryRepo.copy(mockedDataSource)
        // When trying to get a result
        val result = categoryRepo.getCategories(TestData.categoriesIds)

        // Assert it is empty
        assertThat(result).isEmpty()
    }

    @Test
    fun dataSourceSuccess_ReturnArticleCategories() = mainCoroutineRule.runBlockingTest {
        // When trying to get a successful result
        val result = categoryRepo.getArticleCategories(TestData.uiArticle1)

        val expected = TestData.articleCategories

        // Assert it equals our expectations
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun dataSourceFails_ReturnEmptyArticleCategories() = mainCoroutineRule.runBlockingTest {
        // GIVEN data Source that fails to get new result
        val mockedDataSource = mock<FakeCategoryDataSource> {
            on(it.getArticleCategories("fake")).doReturn(Result.Error(Exception()))
        }

        categoryRepo = categoryRepo.copy(mockedDataSource)
        // When trying to get a result
        val result = categoryRepo.getArticleCategories(TestData.uiArticle1)

        // Assert it is empty
        assertThat(result).isEmpty()
    }

    private fun CategoryRepository.copy(dataSource: CategoryDataSource): CategoryRepository {
        return CategoryRepository(dataSource)
    }
}
