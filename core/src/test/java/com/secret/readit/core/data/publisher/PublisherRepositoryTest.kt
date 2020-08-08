/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.publisher

import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.DocumentSnapshot
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.secret.readit.core.MainCoroutineRule
import com.secret.readit.core.TestData
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
    fun allOk_ReturnCurrentUserSuccessfully() = mainCoroutineRule.runBlockingTest { runUiPublisherTest { publisherRepo.getCurrentUser() } }

    @Test
    fun nullUser_ReturnEmptyPublisher() = mainCoroutineRule.runBlockingTest { runUiPublisherTest(nullUser = true) { publisherRepo.getCurrentUser() } }

    @Test
    fun dataSourceFails_ReturnEmptyPublisher() = mainCoroutineRule.runBlockingTest { runUiPublisherTest(mockDataSourceFun = true) { publisherRepo.getCurrentUser() } }

    @Test
    fun allOk_ReturnPublishersSuccessfully() = mainCoroutineRule.runBlockingTest {
        // When trying to get publishers with a number of followers
        val result = publisherRepo.getPublishersWithNumberOfFollowers(emptyList(), 23, 30)

        // Assert all data is ok
        assertThat(publisherRepo.prevSnapshot).isInstanceOf(DocumentSnapshot::class.java)
        assertThat(result).isNotEmpty()
        assertThat(result.size).isEqualTo(1)
        assertThat(result[0].profileImg).isNotNull()
        assertThat(result[0].publisher).isEqualTo(TestData.publisher1)
    }

    @Test
    fun dataSourceFails_ReturnEmptyPublishers() = mainCoroutineRule.runBlockingTest {
        // GIVEN failed dataSource
        val mockedPublisherDataSource = mock<FakePublisherInfoDataSource> {
            on(it.getPublishers(listOf(TestData.publisher1.id), 23, 30, null)).doReturn(Result.Error(Exception()))
        }
        publisherRepo = PublisherRepository(mockedPublisherDataSource, mockedAuthRepository, DummyStorageRepository())

        // When trying to get Publisher with valid number of followers
        val result = publisherRepo.getPublishersWithNumberOfFollowers(emptyList(), 23, 30)

        // Assert We have an empty list
        assertThat(publisherRepo.prevSnapshot).isNull()
        assertThat(result).isEmpty()
    }
    // TODO: refactor the above two tests
    @Test
    fun allOk_ReturnPublisherInfoSuccessfully() = mainCoroutineRule.runBlockingTest {
        runUiPublisherTest {
            publisherRepo.getPublisherInfo(
                TestData.publisher1.id
            )
        }
    }

    @Test
    fun dataSourceFails_ReturnEmptyPublisherInfo() = mainCoroutineRule.runBlockingTest {
        runUiPublisherTest(mockDataSourceFun = true) {
            publisherRepo.getPublisherInfo(
                TestData.publisher1.id
            )
        }
    }

    @Test
    fun allOk_UpdateUserNameSuccessfully() = mainCoroutineRule.runBlockingTest {
        runTest {
            publisherRepo.updateName(
                TestData.publisher1.name
            )
        }
    }

    @Test
    fun nullUser_CannotUpdateUserName() = mainCoroutineRule.runBlockingTest {
        runTest(nullUser = true) {
            publisherRepo.updateName(
                TestData.publisher1.name
            )
        }
    }

    @Test
    fun dataSourceFails_CannotUpdateUserName() = mainCoroutineRule.runBlockingTest {
        runTest(mockDataSourceFun = true) {
            publisherRepo.updateName(
                TestData.publisher1.name
            )
        }
    }

    @Test
    fun allOk_AddNewArticleSuccessfully() = mainCoroutineRule.runBlockingTest {
        runTest {
            publisherRepo.addNewArticle(
                TestData.uiArticle1
            )
        }
    }

    @Test
    fun nullUser_CannotAddArticle() = mainCoroutineRule.runBlockingTest {
        runTest(nullUser = true) {
            publisherRepo.addNewArticle(
                TestData.uiArticle1
            )
        }
    }

    @Test
    fun dataSourceFails_CannotAddArticle() = mainCoroutineRule.runBlockingTest {
        runTest(mockDataSourceFun = true) {
            publisherRepo.addNewArticle(
                TestData.uiArticle1
            )
        }
    }

    @Test
    fun invalidArticle_CannotAddNewArticle() = mainCoroutineRule.runBlockingTest {
        runTest(invalidParameter = true) {
            publisherRepo.addNewArticle(
                TestData.emptyUiArticle
            )
        }
    }

    @Test
    fun allOk_RemoveArticleSuccessfully() = mainCoroutineRule.runBlockingTest {
        runTest {
            publisherRepo.removeArticle(
                TestData.uiArticle1
            )
        }
    }

    @Test
    fun nullUser_CannotRemoveArticle() = mainCoroutineRule.runBlockingTest {
        runTest(nullUser = true) {
            publisherRepo.removeArticle(
                TestData.uiArticle1
            )
        }
    }

    @Test
    fun dataSourceFails_CannotRemoveArticle() = mainCoroutineRule.runBlockingTest {
        runTest(mockDataSourceFun = true) {
            publisherRepo.removeArticle(
                TestData.uiArticle1
            )
        }
    }

    @Test
    fun invalidArticle_CannotRemoveArticle() = mainCoroutineRule.runBlockingTest {
        runTest(invalidParameter = true) {
            publisherRepo.removeArticle(
                TestData.emptyUiArticle
            )
        }
    }

    @Test
    fun allOk_FollowCategorySuccessfully() = mainCoroutineRule.runBlockingTest {
        runTest {
            publisherRepo.followCategory(
                TestData.category1
            )
        }
    }

    @Test
    fun nullUser_CannotFollowCategory() = mainCoroutineRule.runBlockingTest {
        runTest(nullUser = true) {
            publisherRepo.followCategory(
                TestData.category1
            )
        }
    }

    @Test
    fun dataSourceFails_CannotFollowCategory() = mainCoroutineRule.runBlockingTest {
        runTest(mockDataSourceFun = true) {
            publisherRepo.followCategory(
                TestData.category1
            )
        }
    }

    @Test
    fun invalidCategory_CannotFollowCategory() = mainCoroutineRule.runBlockingTest {
        runTest(invalidParameter = true) {
            publisherRepo.followCategory(
                TestData.emptyCategory
            )
        }
    }

    @Test
    fun allOk_unFollowCategorySuccessfully() = mainCoroutineRule.runBlockingTest {
        runTest {
            publisherRepo.unFollowCategory(
                TestData.category1
            )
        }
    }

    @Test
    fun nullUser_CannotunFollowCategory() = mainCoroutineRule.runBlockingTest {
        runTest(nullUser = true) {
            publisherRepo.unFollowCategory(
                TestData.category1
            )
        }
    }

    @Test
    fun dataSourceFails_CannotunFollowCategory() = mainCoroutineRule.runBlockingTest {
        runTest(mockDataSourceFun = true) {
            publisherRepo.unFollowCategory(
                TestData.category1
            )
        }
    }

    @Test
    fun invalidCategory_CannotunFollowCategory() = mainCoroutineRule.runBlockingTest {
        runTest(invalidParameter = true) {
            publisherRepo.unFollowCategory(
                TestData.emptyCategory
            )
        }
    }

    @Test
    fun allOk_FollowPublisherSuccessfully() = mainCoroutineRule.runBlockingTest {
        runTest {
            publisherRepo.followPublisher(
                TestData.uiPublisher1
            )
        }
    }

    @Test
    fun nullUser_CannotFollowPublisher() = mainCoroutineRule.runBlockingTest {
        runTest(nullUser = true) {
            publisherRepo.followPublisher(
                TestData.uiPublisher1
            )
        }
    }

    @Test
    fun dataSourceFails_CannotFollowPublisher() = mainCoroutineRule.runBlockingTest {
        runTest(mockDataSourceFun = true) {
            publisherRepo.followPublisher(
                TestData.uiPublisher1
            )
        }
    }

    @Test
    fun invalidPublisher_CannotFollowPublisher() = mainCoroutineRule.runBlockingTest {
        runTest(invalidParameter = true) {
            publisherRepo.followPublisher(
                TestData.emptyUiPublisher
            )
        }
    }

    @Test
    fun allOk_unFollowPublisherSuccessfully() = mainCoroutineRule.runBlockingTest {
        runTest {
            publisherRepo.unFollowPublisher(
                TestData.uiPublisher1
            )
        }
    }

    @Test
    fun nullUser_CannotunFollowPublisher() = mainCoroutineRule.runBlockingTest {
        runTest(nullUser = true) {
            publisherRepo.unFollowPublisher(
                TestData.uiPublisher1
            )
        }
    }

    @Test
    fun dataSourceFails_CannotunFollowPublisher() = mainCoroutineRule.runBlockingTest {
        runTest(mockDataSourceFun = true) {
            publisherRepo.unFollowPublisher(
                TestData.uiPublisher1
            )
        }
    }

    @Test
    fun invalidPublisher_CannotunFollowPublisher() = mainCoroutineRule.runBlockingTest {
        runTest(invalidParameter = true) {
            publisherRepo.unFollowPublisher(
                TestData.emptyUiPublisher
            )
        }
    }

    /**
     * Refactor boilerplate to this private fun
     * it takes 4 parameters, only 1 mandatory Param is [funUnderTest] which is function under test
     */
    private suspend fun runTest(
        nullUser: Boolean = false,
        mockDataSourceFun: Boolean = false,
        invalidParameter: Boolean = false,
        funUnderTest: suspend PublisherRepository.() -> Boolean
    ) {
        if (nullUser) mockedAuthRepository = mock { on(it.getId()).doReturn(null) } // GIVEN no signed-in User

        val mockedPublisherDataSource = if (mockDataSourceFun) DummyPublisherDataSource() else FakePublisherInfoDataSource() // GIVEN failed dataSource

        // Satisfy dependencies based on conditions
        publisherRepo = PublisherRepository(mockedPublisherDataSource, mockedAuthRepository, DummyStorageRepository())
        val result = publisherRepo.funUnderTest()
        // Assert it all goes as intended
        if (nullUser || mockDataSourceFun || invalidParameter) assertThat(result).isFalse() else assertThat(result).isTrue()
    }

    /** the same idea as the function above */
    private suspend fun runUiPublisherTest(
        nullUser: Boolean = false,
        mockDataSourceFun: Boolean = false,
        invalidParameter: Boolean = false,
        funUnderTest: suspend PublisherRepository.() -> UiPublisher
    ) {
        if (nullUser) mockedAuthRepository = mock { on(it.getId()).doReturn(null) } // GIVEN no signed-in User

        val mockedPublisherDataSource = if (mockDataSourceFun) DummyPublisherDataSource() else FakePublisherInfoDataSource() // GIVEN failed dataSource

        // Satisfy dependencies based on conditions
        publisherRepo = PublisherRepository(mockedPublisherDataSource, mockedAuthRepository, DummyStorageRepository())
        val result = publisherRepo.funUnderTest()
        // Assert it all goes as intended
        assertThat(result).isInstanceOf(UiPublisher::class.java)
        if (nullUser || mockDataSourceFun || invalidParameter) {
            assertThat(result.publisher).isEqualTo(TestData.emptyPublisher)
            assertThat(result.profileImg).isNull()
        } else {
            assertThat(result.publisher).isEqualTo(TestData.publisher1)
            assertThat(result.profileImg).isNotNull()
        }
    }
}
