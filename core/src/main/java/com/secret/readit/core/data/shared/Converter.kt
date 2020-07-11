/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.shared

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.secret.readit.core.di.DefaultDispatcher
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import javax.inject.Inject

/**
 * Helper to convert from & to InputStream
 */
class Converter @Inject constructor(@DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher) {

    val coroutineScope = CoroutineScope(Job() + defaultDispatcher)

    /**
     * Provide FileInputStream From the given path
     */
    fun pathToInputStream(path: String): InputStream{
        val file = File(path)
        return FileInputStream(file)
    }

    /**
     * decode an input stream into a bitmap,
     *
     * @return decoded Bitmap or null if decoding failed
     */
    fun inputStreamToBitmap(inStream: InputStream): Bitmap?{
        var bitmap: Bitmap? = null
        try {
            coroutineScope.launch {
                if (isActive){
                    bitmap = BitmapFactory.decodeStream(inStream)
                }
            }
        } catch (cancelEx: CancellationException) {
            Timber.e("Coroutine cancelled No need to complete the work")
            throw cancelEx
        }
        return bitmap
    }
}