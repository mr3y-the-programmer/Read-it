/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.publisher

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.secret.readit.core.MainCoroutineRule
import com.secret.readit.core.data.auth.AuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.google.common.truth.Truth.assertThat
import com.secret.readit.core.data.shared.DummyStorageRepository
import com.secret.readit.core.result.Result
import com.secret.readit.core.uimodels.UiPublisher
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
        //When trying to get CurrentUser
        val result = publisherRepo.getCurrentUser()

        //Assert all data is ok
        assertThat(result).isInstanceOf(UiPublisher::class.java)
        assertThat(result.publisher).isEqualTo(TestData.publisher1)
        assertThat(result.profileImg).isNotNull()
    }

    @Test
    fun nullUser_ReturnEmptyPublisher() = mainCoroutineRule.runBlockingTest {
        //GIVEN no signed-in User
        mockedAuthRepository = mock {
            on(it.getId()).doReturn(null)
        }
        publisherRepo = PublisherRepository(FakePublisherInfoDataSource(), mockedAuthRepository, DummyStorageRepository())

        //When trying to get CurrentUser
        val result = publisherRepo.getCurrentUser()

        //Assert We have an empty publisher
        assertThat(result).isInstanceOf(UiPublisher::class.java)
        assertThat(result.publisher).isEqualTo(TestData.emptyPublisher)
        assertThat(result.profileImg).isNull()
    }

    @Test
    fun dataSourceFails_ReturnEmptyPublisher() = mainCoroutineRule.runBlockingTest {
        //GIVEN failed dataSource
        val mockedPublisherDataSource = mock<FakePublisherInfoDataSource> {
            on(it.getPublisher(TestData.publisher1.id)).doReturn(Result.Error(Exception()))
        }
        publisherRepo = PublisherRepository(mockedPublisherDataSource, mockedAuthRepository, DummyStorageRepository())

        //When trying to get CurrentUser
        val result = publisherRepo.getCurrentUser()

        //Assert We have an empty publisher
        assertThat(result).isInstanceOf(UiPublisher::class.java)
        assertThat(result.publisher).isEqualTo(TestData.emptyPublisher)
        assertThat(result.profileImg).isNull()
    }
}