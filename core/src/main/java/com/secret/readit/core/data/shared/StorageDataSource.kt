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

/**
 * Blueprint for cloud storage operations for publishers/articles...etc
 */
interface StorageDataSource {

    /**
     * Upload the bitmap at this local-file system path
     *
     * @return the Uri to download this bitmap later, **NOTE**: it can be revoked from firebase console
     */
    suspend fun uploadBitmap(id: articleId, imgPath: String): Result<Uri>

    /**
     * Download bitmap that this uri represent
     *
     * @return bitmap
     */
    suspend fun downloadBitmap(uri: Uri): Result<Bitmap>
}