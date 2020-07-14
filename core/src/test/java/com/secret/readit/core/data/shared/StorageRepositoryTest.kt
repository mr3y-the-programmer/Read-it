/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.shared

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
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
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/*
   Robolectric test
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(minSdk = Build.VERSION_CODES.LOLLIPOP, maxSdk = Build.VERSION_CODES.Q, manifest = Config.NONE)
class StorageRepositoryTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    // Object Under test
    private lateinit var storageRepo: StorageRepository

    @Before
    fun setUp() {
        storageRepo = StorageRepository(FakeStorageDataSource())
    }

    @Test
    fun dataSourceSuccess_ReturnBitmap() = mainCoroutineRule.runBlockingTest {
        //GIVEN a valid uri
        val validUri = Uri.parse(validStringUri)

        //When trying to download image with any valid Uri
        val result = storageRepo.downloadImg(validUri, "not needed when uri valid")

        //Assert we have a bitmap value
        assertThat(result).isNotNull()
        //In other words
        assertThat(result).isInstanceOf(Bitmap::class.java)
    }

    @Test
    fun dataSourceFails_ReturnNull() = mainCoroutineRule.runBlockingTest {
        //GIVEN a valid uri, dataSource that fails to get data
        val validUri = Uri.parse(validStringUri)
        val mockedDataSource = mock<FakeStorageDataSource> {
            on(it.downloadBitmap(validUri)).doReturn(Result.Error(Exception()))
        }

        storageRepo = StorageRepository(mockedDataSource)

        //When trying to get a result
        val result = storageRepo.downloadImg(validUri, "not needed")

        //Assert data is null, although url is valid
        assertThat(result).isNull()
    }

    companion object {
        const val validStringUri = "https://firebasestorage.googleapis.com/v0/b/read-it-b9c8b.appspot.com/" +
                "o/publishers%2Faccount_default.png?alt=media&token=1502d0e1-c30f-4d45-997d-e39ac5af62ba"
    }
}