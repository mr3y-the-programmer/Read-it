/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.publisher

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.secret.readit.core.MainCoroutineRule
import com.secret.readit.core.data.auth.AuthRepository
import com.secret.readit.core.data.shared.DummyStorageRepository
import com.secret.readit.core.result.Result
import com.secret.readit.core.uimodels.UiPublisher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class PublisherRepositoryTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    // Object Under test
    private lateinit var publisherRepo: PublisherRepository

    private lateinit var mockedAuthRepository: AuthRepository

    @Before
    fun setUp() {
        mockedAuthRepository = mock {
            on(it.getId()).doReturn(TestData.publisher1.id)
        }

        publisherRepo = PublisherRepository(FakePublisherInfoDataSource(), mockedAuthRepository, DummyStorageRepository())
    }

    @Test
    fun allOk_ReturnCurrentUserSuccessfully() = mainCoroutineRule.runBlockingTest {
        // When trying to get CurrentUser
        val result = publisherRepo.getCurrentUser()

        // Assert all data is ok
        assertThat(result).isInstanceOf(UiPublisher::class.java)
        assertThat(result.publisher).isEqualTo(TestData.publisher1)
        assertThat(result.profileImg).isNotNull()
    }

    @Test
    fun nullUser_ReturnEmptyPublisher() = mainCoroutineRule.runBlockingTest {
        // GIVEN no signed-in User
        mockedAuthRepository = mock {
            on(it.getId()).doReturn(null)
        }
        publisherRepo = PublisherRepository(FakePublisherInfoDataSource(), mockedAuthRepository, DummyStorageRepository())

        // When trying to get CurrentUser
        val result = publisherRepo.getCurrentUser()

        // Assert We have an empty publisher
        assertThat(result).isInstanceOf(UiPublisher::class.java)
        assertThat(result.publisher).isEqualTo(TestData.emptyPublisher)
        assertThat(result.profileImg).isNull()
    }

    @Test
    fun dataSourceFails_ReturnEmptyPublisher() = mainCoroutineRule.runBlockingTest {
        // GIVEN failed dataSource
        val mockedPublisherDataSource = mock<FakePublisherInfoDataSource> {
            on(it.getPublisher(TestData.publisher1.id)).doReturn(Result.Error(Exception()))
        }
        publisherRepo = PublisherRepository(mockedPublisherDataSource, mockedAuthRepository, DummyStorageRepository())

        // When trying to get CurrentUser
        val result = publisherRepo.getCurrentUser()

        // Assert We have an empty publisher
        assertThat(result).isInstanceOf(UiPublisher::class.java)
        assertThat(result.publisher).isEqualTo(TestData.emptyPublisher)
        assertThat(result.profileImg).isNull()
    }

    @Test
    fun allOk_UpdateUserNameSuccessfully() = mainCoroutineRule.runBlockingTest {
        // When trying to update CurrentUser name
        val result = publisherRepo.updateName(TestData.publisher1.name)

        // Assert process is success
        assertThat(result).isTrue()
    }

    @Test
    fun nullUser_CannotUpdateUserName() = mainCoroutineRule.runBlockingTest {
        // GIVEN no signed-in User
        mockedAuthRepository = mock {
            on(it.getId()).doReturn(null)
        }
        publisherRepo = PublisherRepository(FakePublisherInfoDataSource(), mockedAuthRepository, DummyStorageRepository())

        // When trying to update name
        val result = publisherRepo.updateName(TestData.publisher1.name)

        // Assert We return false(failed)
        assertThat(result).isFalse()
    }

    @Test
    fun dataSourceFails_CannotUpdateUserName() = mainCoroutineRule.runBlockingTest {
        // GIVEN failed dataSource(i.e No Internet Connection)
        val mockedPublisherDataSource = mock<FakePublisherInfoDataSource> {
            on(it.setDisplayName(TestData.publisher1.name, TestData.publisher1.id)).doReturn(Result.Error(Exception()))
        }
        publisherRepo = PublisherRepository(mockedPublisherDataSource, mockedAuthRepository, DummyStorageRepository())

        // When trying to update name
        val result = publisherRepo.updateName(TestData.publisher1.name)

        // Assert We failed
        assertThat(result).isFalse()
    }

    @Test
    fun allOk_AddNewArticleSuccessfully() = mainCoroutineRule.runBlockingTest { runTest{ publisherRepo.addNewArticle(TestData.article1) }}

    @Test
    fun nullUser_CannotAddArticle() = mainCoroutineRule.runBlockingTest { runTest(nullUser = true){ publisherRepo.addNewArticle(TestData.article1) }}

    @Test
    fun dataSourceFails_CannotAddArticle() = mainCoroutineRule.runBlockingTest { runTest(mockDataSourceFun = true){ publisherRepo.addNewArticle(TestData.article1) }}

    @Test
    fun invalidArticle_CannotAddNewArticle() = mainCoroutineRule.runBlockingTest { runTest(invalidParameter = true){ publisherRepo.addNewArticle(TestData.emptyArticle) }}

    @Test
    fun allOk_RemoveArticleSuccessfully() = mainCoroutineRule.runBlockingTest { runTest{ publisherRepo.removeArticle(TestData.article1) }}

    @Test
    fun nullUser_CannotRemoveArticle() = mainCoroutineRule.runBlockingTest { runTest(nullUser = true){ publisherRepo.removeArticle(TestData.article1) }}

    @Test
    fun dataSourceFails_CannotRemoveArticle() = mainCoroutineRule.runBlockingTest { runTest(mockDataSourceFun = true){ publisherRepo.removeArticle(TestData.article1) }}

    @Test
    fun invalidArticle_CannotRemoveArticle() = mainCoroutineRule.runBlockingTest { runTest(invalidParameter = true){ publisherRepo.removeArticle(TestData.emptyArticle) }}

    @Test
    fun allOk_FollowCategorySuccessfully() = mainCoroutineRule.runBlockingTest { runTest{publisherRepo.followCategory(TestData.category1)}}

    @Test
    fun nullUser_CannotFollowCategory() = mainCoroutineRule.runBlockingTest { runTest(nullUser = true){publisherRepo.followCategory(TestData.category1)}}

    @Test
    fun dataSourceFails_CannotFollowCategory() = mainCoroutineRule.runBlockingTest { runTest(mockDataSourceFun = true){publisherRepo.followCategory(TestData.category1)}}

    @Test
    fun invalidCategory_CannotFollowCategory() = mainCoroutineRule.runBlockingTest { runTest(invalidParameter = true){publisherRepo.followCategory(TestData.emptyCategory)}}

    @Test
    fun allOk_unFollowCategorySuccessfully() = mainCoroutineRule.runBlockingTest { runTest{publisherRepo.unFollowCategory(TestData.category1)}}

    @Test
    fun nullUser_CannotunFollowCategory() = mainCoroutineRule.runBlockingTest { runTest(nullUser = true){publisherRepo.unFollowCategory(TestData.category1)} }

    @Test
    fun dataSourceFails_CannotunFollowCategory() = mainCoroutineRule.runBlockingTest { runTest(mockDataSourceFun = true){publisherRepo.unFollowCategory(TestData.category1)}}

    @Test
    fun invalidCategory_CannotunFollowCategory() = mainCoroutineRule.runBlockingTest { runTest(invalidParameter = true){publisherRepo.unFollowCategory(TestData.emptyCategory)}}

    @Test
    fun allOk_FollowPublisherSuccessfully() = mainCoroutineRule.runBlockingTest { runTest{publisherRepo.followPublisher(TestData.uiPublisher1)}}

    @Test
    fun nullUser_CannotFollowPublisher() = mainCoroutineRule.runBlockingTest { runTest(nullUser = true){publisherRepo.followPublisher(TestData.uiPublisher1)}}

    @Test
    fun dataSourceFails_CannotFollowPublisher() = mainCoroutineRule.runBlockingTest { runTest(mockDataSourceFun = true){publisherRepo.followPublisher(TestData.uiPublisher1)}}

    @Test
    fun invalidPublisher_CannotFollowPublisher() = mainCoroutineRule.runBlockingTest { runTest(invalidParameter = true){publisherRepo.followPublisher(TestData.emptyUiPublisher)}}

    @Test
    fun allOk_unFollowPublisherSuccessfully() = mainCoroutineRule.runBlockingTest { runTest{ publisherRepo.unFollowPublisher(TestData.uiPublisher1) }}

    @Test
    fun nullUser_CannotunFollowPublisher() = mainCoroutineRule.runBlockingTest { runTest(nullUser = true){ publisherRepo.unFollowPublisher(TestData.uiPublisher1) }}

    @Test
    fun dataSourceFails_CannotunFollowPublisher() = mainCoroutineRule.runBlockingTest { runTest(mockDataSourceFun = true){ publisherRepo.unFollowPublisher(TestData.uiPublisher1) }}

    @Test
    fun invalidPublisher_CannotunFollowPublisher() = mainCoroutineRule.runBlockingTest { runTest(invalidParameter = true){ publisherRepo.unFollowPublisher(TestData.emptyUiPublisher) }}

    /**
     * Refactor boilerplate to this private fun
     * it takes 4 parameters the only two mandatory Param is [funUnderTest] which is function under test
     */
    private suspend fun runTest(
        nullUser: Boolean = false,
        mockDataSourceFun: Boolean = false,
        invalidParameter: Boolean = false,
        funUnderTest: suspend PublisherRepository.() -> Boolean)
    {
        if (nullUser) mockedAuthRepository = mock { on(it.getId()).doReturn(null) } // GIVEN no signed-in User

        val mockedPublisherDataSource = if (mockDataSourceFun) DummyPublisherDataSource() else FakePublisherInfoDataSource() // GIVEN failed dataSource

        //Satisfy dependencies based on conditions
        publisherRepo = PublisherRepository(mockedPublisherDataSource, mockedAuthRepository, DummyStorageRepository())
        val result = publisherRepo.funUnderTest()
        // Assert it all goes as intended
        if (nullUser || mockDataSourceFun || invalidParameter) assertThat(result).isFalse() else assertThat(result).isTrue()
    }
}
