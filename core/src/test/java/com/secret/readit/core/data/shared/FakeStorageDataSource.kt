/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.shared

import android.graphics.Bitmap
import android.net.Uri
import com.secret.readit.core.result.Result
import com.secret.readit.model.articleId

open class FakeStorageDataSource: StorageDataSource {
    override suspend fun uploadBitmap(id: articleId, imgPath: String): Result<Uri> {
        TODO("Not yet implemented")
    }

    override suspend fun downloadBitmap(uri: Uri): Result<Bitmap> {
        TODO("Not yet implemented")
    }
}