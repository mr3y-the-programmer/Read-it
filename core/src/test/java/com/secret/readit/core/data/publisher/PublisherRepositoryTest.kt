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

    @Test
    fun allOk_UpdateUserNameSuccessfully() = mainCoroutineRule.runBlockingTest {
        //When trying to update CurrentUser name
        val result = publisherRepo.updateName(TestData.publisher1.name)

        //Assert process is success
        assertThat(result).isTrue()
    }

    @Test
    fun nullUser_CannotUpdateUserName() = mainCoroutineRule.runBlockingTest {
        //GIVEN no signed-in User
        mockedAuthRepository = mock {
            on(it.getId()).doReturn(null)
        }
        publisherRepo = PublisherRepository(FakePublisherInfoDataSource(), mockedAuthRepository, DummyStorageRepository())

        //When trying to update name
        val result = publisherRepo.updateName(TestData.publisher1.name)

        //Assert We return false(failed)
        assertThat(result).isFalse()
    }

    @Test
    fun dataSourceFails_CannotUpdateUserName() = mainCoroutineRule.runBlockingTest {
        //GIVEN failed dataSource(i.e No Internet Connection)
        val mockedPublisherDataSource = mock<FakePublisherInfoDataSource> {
            on(it.setDisplayName(TestData.publisher1.name, TestData.publisher1.id)).doReturn(Result.Error(Exception()))
        }
        publisherRepo = PublisherRepository(mockedPublisherDataSource, mockedAuthRepository, DummyStorageRepository())

        //When trying to update name
        val result = publisherRepo.updateName(TestData.publisher1.name)

        //Assert We failed
        assertThat(result).isFalse()
    }

    @Test
    fun allOk_AddNewArticleSuccessfully() = mainCoroutineRule.runBlockingTest {
        //When trying to publish new article
        val result = publisherRepo.addNewArticle(TestData.article1)

        //Assert process is success
        assertThat(result).isTrue()
    }

    @Test
    fun nullUser_CannotAddNewArticle() = mainCoroutineRule.runBlockingTest {
        //GIVEN no signed-in User
        mockedAuthRepository = mock {
            on(it.getId()).doReturn(null)
        }
        publisherRepo = PublisherRepository(FakePublisherInfoDataSource(), mockedAuthRepository, DummyStorageRepository())

        //When trying to publish new article
        val result = publisherRepo.addNewArticle(TestData.article1)

        //Assert We return false(failed)
        assertThat(result).isFalse()
    }

    @Test
    fun dataSourceFails_CannotAddNewArticle() = mainCoroutineRule.runBlockingTest {
        //GIVEN failed dataSource(i.e No Internet Connection)
        val mockedPublisherDataSource = mock<FakePublisherInfoDataSource> {
            on(it.addNewArticleId(TestData.article1.id, TestData.publisher1.id)).doReturn(Result.Error(Exception()))
        }
        publisherRepo = PublisherRepository(mockedPublisherDataSource, mockedAuthRepository, DummyStorageRepository())

        //When trying to publish new article
        val result = publisherRepo.addNewArticle(TestData.article1)

        //Assert We failed
        assertThat(result).isFalse()
    }

    @Test
    fun invalidArticle_CannotAddNewArticle() = mainCoroutineRule.runBlockingTest {
        //When trying to publish invalid article
        val result = publisherRepo.addNewArticle(TestData.emptyArticle)

        //Assert We return false(failed)
        assertThat(result).isFalse()
    }

    @Test
    fun allOk_RemoveArticleSuccessfully() = mainCoroutineRule.runBlockingTest {
        //When trying to remove article
        val result = publisherRepo.removeArticle(TestData.article1)

        //Assert process is success
        assertThat(result).isTrue()
    }

    @Test
    fun nullUser_CannotRemoveArticle() = mainCoroutineRule.runBlockingTest {
        //GIVEN no signed-in User
        mockedAuthRepository = mock {
            on(it.getId()).doReturn(null)
        }
        publisherRepo = PublisherRepository(FakePublisherInfoDataSource(), mockedAuthRepository, DummyStorageRepository())

        //When trying to remove article
        val result = publisherRepo.removeArticle(TestData.article1)

        //Assert We return false(failed)
        assertThat(result).isFalse()
    }

    @Test
    fun dataSourceFails_CannotRemoveArticle() = mainCoroutineRule.runBlockingTest {
        //GIVEN failed dataSource(i.e No Internet Connection)
        val mockedPublisherDataSource = mock<FakePublisherInfoDataSource> {
            on(it.removeExistingArticleId(TestData.article1.id, TestData.publisher1.id)).doReturn(Result.Error(Exception()))
        }
        publisherRepo = PublisherRepository(mockedPublisherDataSource, mockedAuthRepository, DummyStorageRepository())

        //When trying to remove article
        val result = publisherRepo.removeArticle(TestData.article1)

        //Assert We failed
        assertThat(result).isFalse()
    }

    @Test
    fun invalidArticle_CannotRemoveArticle() = mainCoroutineRule.runBlockingTest {
        //When trying to remove invalid article
        val result = publisherRepo.removeArticle(TestData.emptyArticle)

        //Assert We return false(failed)
        assertThat(result).isFalse()
    }

    @Test
    fun allOk_FollowCategorySuccessfully() = mainCoroutineRule.runBlockingTest {
        //When trying to follow category
        val result = publisherRepo.followCategory(TestData.category1)

        //Assert process is success
        assertThat(result).isTrue()
    }

    @Test
    fun nullUser_CannotFollowCategory() = mainCoroutineRule.runBlockingTest {
        //GIVEN no signed-in User
        mockedAuthRepository = mock {
            on(it.getId()).doReturn(null)
        }
        publisherRepo = PublisherRepository(FakePublisherInfoDataSource(), mockedAuthRepository, DummyStorageRepository())

        //When trying to follow category
        val result = publisherRepo.followCategory(TestData.category1)

        //Assert We return false(failed)
        assertThat(result).isFalse()
    }

    @Test
    fun dataSourceFails_CannotFollowCategory() = mainCoroutineRule.runBlockingTest {
        //GIVEN failed dataSource(i.e No Internet Connection)
        val mockedPublisherDataSource = mock<FakePublisherInfoDataSource> {
            on(it.addNewCategoryId(TestData.category1.id, TestData.publisher1.id)).doReturn(Result.Error(Exception()))
        }
        publisherRepo = PublisherRepository(mockedPublisherDataSource, mockedAuthRepository, DummyStorageRepository())

        //When trying to follow category
        val result = publisherRepo.followCategory(TestData.category1)

        //Assert We failed
        assertThat(result).isFalse()
    }

    @Test
    fun invalidCategory_CannotFollowCategory() = mainCoroutineRule.runBlockingTest {
        //When trying to follow invalid category
        val result = publisherRepo.followCategory(TestData.emptyCategory)

        //Assert We return false(failed)
        assertThat(result).isFalse()
    }

    @Test
    fun allOk_unFollowCategorySuccessfully() = mainCoroutineRule.runBlockingTest {
        //When trying to unFollow category
        val result = publisherRepo.unFollowCategory(TestData.category1)

        //Assert process is success
        assertThat(result).isTrue()
    }

    @Test
    fun nullUser_CannotunFollowCategory() = mainCoroutineRule.runBlockingTest {
        //GIVEN no signed-in User
        mockedAuthRepository = mock {
            on(it.getId()).doReturn(null)
        }
        publisherRepo = PublisherRepository(FakePublisherInfoDataSource(), mockedAuthRepository, DummyStorageRepository())

        //When trying to unFollow category
        val result = publisherRepo.unFollowCategory(TestData.category1)

        //Assert We return false(failed)
        assertThat(result).isFalse()
    }

    @Test
    fun dataSourceFails_CannotunFollowCategory() = mainCoroutineRule.runBlockingTest {
        //GIVEN failed dataSource(i.e No Internet Connection)
        val mockedPublisherDataSource = mock<FakePublisherInfoDataSource> {
            on(it.unFollowExistingCategoryId(TestData.category1.id, TestData.publisher1.id)).doReturn(Result.Error(Exception()))
        }
        publisherRepo = PublisherRepository(mockedPublisherDataSource, mockedAuthRepository, DummyStorageRepository())

        //When trying to unFollow category
        val result = publisherRepo.unFollowCategory(TestData.category1)

        //Assert We failed
        assertThat(result).isFalse()
    }

    @Test
    fun invalidCategory_CannotunFollowCategory() = mainCoroutineRule.runBlockingTest {
        //When trying to unFollow invalid category
        val result = publisherRepo.unFollowCategory(TestData.emptyCategory)

        //Assert We return false(failed)
        assertThat(result).isFalse()
    }

    @Test
    fun allOk_FollowPublisherSuccessfully() = mainCoroutineRule.runBlockingTest {
        //When trying to follow publisher
        val result = publisherRepo.followPublisher(UiPublisher(TestData.publisher1, null))

        //Assert process is success
        assertThat(result).isTrue()
    }

    @Test
    fun nullUser_CannotFollowPublisher() = mainCoroutineRule.runBlockingTest {
        //GIVEN no signed-in User
        mockedAuthRepository = mock {
            on(it.getId()).doReturn(null)
        }
        publisherRepo = PublisherRepository(FakePublisherInfoDataSource(), mockedAuthRepository, DummyStorageRepository())

        //When trying to follow publisher
        val result = publisherRepo.followPublisher(UiPublisher(TestData.publisher1, null))

        //Assert We return false(failed)
        assertThat(result).isFalse()
    }

    @Test
    fun dataSourceFails_CannotFollowPublisher() = mainCoroutineRule.runBlockingTest {
        //GIVEN failed dataSource(i.e No Internet Connection)
        val mockedPublisherDataSource = mock<FakePublisherInfoDataSource> {
            on(it.follow(TestData.publisher2.id, TestData.publisher1.id)).doReturn(Result.Error(Exception()))
        }
        publisherRepo = PublisherRepository(mockedPublisherDataSource, mockedAuthRepository, DummyStorageRepository())

        //When trying to follow publisher
        val result = publisherRepo.followPublisher(UiPublisher(TestData.publisher2, null))

        //Assert We failed
        assertThat(result).isFalse()
    }

    @Test
    fun invalidPublisher_CannotFollowPublisher() = mainCoroutineRule.runBlockingTest {
        //GIVEN failed dataSource(i.e user/publisher isn't exist)
        val mockedPublisherDataSource = mock<FakePublisherInfoDataSource> {
            on(it.getPublisherId(PubImportantInfo("not exist", "not exist", -1))).doReturn(Result.Error(Exception()))
        }
        publisherRepo = PublisherRepository(mockedPublisherDataSource, mockedAuthRepository, DummyStorageRepository())

        //When trying to follow publisher
        val result = publisherRepo.followPublisher(UiPublisher(TestData.publisher1, null))

        //Assert We return false(failed)
        assertThat(result).isFalse()
    }

    @Test
    fun allOk_unFollowPublisherSuccessfully() = mainCoroutineRule.runBlockingTest {
        //When trying to unfollow publisher
        val result = publisherRepo.unFollowPublisher(UiPublisher(TestData.publisher1, null))

        //Assert process is success
        assertThat(result).isTrue()
    }

    @Test
    fun nullUser_CannotunFollowPublisher() = mainCoroutineRule.runBlockingTest {
        //GIVEN no signed-in User
        mockedAuthRepository = mock {
            on(it.getId()).doReturn(null)
        }
        publisherRepo = PublisherRepository(FakePublisherInfoDataSource(), mockedAuthRepository, DummyStorageRepository())

        //When trying to unFollow publisher
        val result = publisherRepo.unFollowPublisher(UiPublisher(TestData.publisher1, null))

        //Assert We return false(failed)
        assertThat(result).isFalse()
    }

    @Test
    fun dataSourceFails_CannotunFollowPublisher() = mainCoroutineRule.runBlockingTest {
        //GIVEN failed dataSource(i.e No Internet Connection)
        val mockedPublisherDataSource = mock<FakePublisherInfoDataSource> {
            on(it.unFollow(TestData.publisher2.id, TestData.publisher1.id)).doReturn(Result.Error(Exception()))
        }
        publisherRepo = PublisherRepository(mockedPublisherDataSource, mockedAuthRepository, DummyStorageRepository())

        //When trying to unFollow publisher
        val result = publisherRepo.unFollowPublisher(UiPublisher(TestData.publisher2, null))

        //Assert We failed
        assertThat(result).isFalse()
    }

    @Test
    fun invalidPublisher_CannotunFollowPublisher() = mainCoroutineRule.runBlockingTest {
        //GIVEN failed dataSource(i.e user/publisher isn't exist)
        val mockedPublisherDataSource = mock<FakePublisherInfoDataSource> {
            on(it.getPublisherId(PubImportantInfo("not exist", "not exist", -1))).doReturn(Result.Error(Exception()))
        }
        publisherRepo = PublisherRepository(mockedPublisherDataSource, mockedAuthRepository, DummyStorageRepository())

        //When trying to unFollow publisher
        val result = publisherRepo.unFollowPublisher(UiPublisher(TestData.publisher1, null))

        //Assert We return false(failed)
        assertThat(result).isFalse()
    }
}