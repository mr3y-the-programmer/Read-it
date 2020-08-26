/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.shared

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.secret.readit.core.di.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.InputStream
import javax.inject.Inject

/**
 * Helper to convert from & to InputStream
 */
class Converter @Inject constructor(@DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher) {

    /**
     * Provide Uri From the given local-file system path
     */
    fun pathToUri(path: String): Uri {
        return Uri.fromFile(File(path))
    }

    /**
     * decode an input stream into a bitmap,
     *
     * @return decoded Bitmap or null if decoding failed
     */
    fun inputStreamToBitmap(inStream: InputStream): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            CoroutineScope(Job() + defaultDispatcher).launch {
                bitmap = BitmapFactory.decodeStream(inStream)
            }
        } catch (ex: IllegalArgumentException) {
            Timber.e("Bitmap configuration has something wrong, cause: ${ex.message}")
            return null
        }
        return bitmap
    }
}
