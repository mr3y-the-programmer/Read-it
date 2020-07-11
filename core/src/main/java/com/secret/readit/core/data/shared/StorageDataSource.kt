/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.shared

import android.net.Uri
import com.secret.readit.core.result.Result
import com.secret.readit.model.articleId
import java.io.InputStream

/**
 * Blueprint for cloud storage operations for publishers/articles...etc
 */
interface StorageDataSource {

    /**
     * Upload the bitmap's input stream to path within 'articles/articleId/'
     *
     * @return the download Uri
     */
    suspend fun uploadBitmap(id: articleId, inputStream: InputStream): Result<Uri>

    /**
     * Download bitmap that this uri represent
     *
     * @return bitmap's input stream
     */
    suspend fun downloadBitmap(id: articleId, uri: Uri): Result<InputStream>
}