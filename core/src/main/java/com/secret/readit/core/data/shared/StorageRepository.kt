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
import com.secret.readit.core.result.succeeded
import com.secret.readit.model.articleId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Any consumers should interact with this Repo not with DataSource directly to avoid boilerplate
 * Rule: -forward operations to and from StorageDataSource
 */
@Singleton
open class StorageRepository @Inject constructor(private val storageDataSource: StorageDataSource) {

    /**
     * download Img with this Uri or fallback to defaultValue if uri is null,
     * @param imgUri: the Uri of required image to download
     * @param defaultValue: String represent the url of default img, this param should never be null
     */
    open suspend fun downloadImg(imgUri: Uri?, defaultValue: String): Bitmap?{
        val default = Uri.parse(defaultValue)
        val result = storageDataSource.downloadBitmap(imgUri ?: default)

        return if (result.succeeded) (result as Result.Success).data else null
    }

    open suspend fun uploadImg(id: articleId, imgPath: String): Uri?{
        val result = storageDataSource.uploadBitmap(id, imgPath)

        return if (result.succeeded) (result as Result.Success).data else null
    }
}