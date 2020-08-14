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
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DummyStorageRepository : StorageRepository(FakeStorageDataSource()) {
    override suspend fun downloadImg(imgUri: Uri?, defaultValue: String): Bitmap? {
        return mock {
            // no-op
        }
    }

    override suspend fun uploadImg(
        id: String,
        imgPath: String,
        destination: Destination
    ): Uri? {
        return mock {
            // no-op
        }
    }
}
