/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.shared

import android.graphics.Bitmap
import android.net.Uri
import com.nhaarman.mockitokotlin2.mock
import com.secret.readit.core.result.Result
import com.secret.readit.model.articleId
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
open class FakeStorageDataSource : StorageDataSource {

    override suspend fun uploadBitmap(id: articleId, imgPath: String): Result<Uri> {
        val mockedUri = mock<Uri> {
            // no-op
        }
        return Result.Success(mockedUri)
    }

    override suspend fun downloadBitmap(uri: Uri): Result<Bitmap> {
        val mockedBitmap = mock<Bitmap> {
            // no-op
        }
        return Result.Success(mockedBitmap)
    }
}
