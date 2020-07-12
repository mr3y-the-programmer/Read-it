/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.shared

import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.secret.readit.core.data.utils.wrapInCoroutineCancellable
import com.secret.readit.core.di.IoDispatcher
import com.secret.readit.core.result.Result
import com.secret.readit.model.articleId
import dagger.Lazy
import kotlinx.coroutines.CoroutineDispatcher
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Our DefaultStorageDataSource has one responsibility, interact directly with storage to upload/download images
 */
class DefaultStorageDataSource @Inject constructor(
    private val storage: FirebaseStorage,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val converter: Lazy<Converter>
) : StorageDataSource {

    /**
     * upload img with this path and return a uri that can be used to download it later
     */
    override suspend fun uploadBitmap(id: articleId, imgPath: String): Result<Uri> {
        return wrapInCoroutineCancellable(ioDispatcher) { continuation ->
            val root = storage.reference // root reference
            val ref = root.child(ARTICLES_DIR).child(id)

            val pathInStream = converter.get().pathToInputStream(imgPath)
            ref.putStream(pathInStream)
                .continueWithTask {
                    if (it.isSuccessful) {
                        ref.downloadUrl
                    } else {
                        Timber.e("task isn't successful, cause: ${it.exception?.message}")
                        throw it.exception?.cause!!
                    }
                }.addOnSuccessListener {
                    if (continuation.isActive) {
                        Timber.d("Uploading Image success")
                        continuation.resume(Result.Success(it))
                    } else {
                        Timber.d("Continuation is no longer active")
                    }
                }.addOnFailureListener {
                    Timber.e("Failed to upload Image, cause: ${it.message}")
                    continuation.resumeWithException(it)
                }
        }
    }

    /**
     * Download the Bitmap represented with [uri]
     */
    override suspend fun downloadBitmap(uri: Uri): Result<Bitmap> {
        return wrapInCoroutineCancellable(ioDispatcher) { continuation ->
            storage.getReferenceFromUrl(uri.toString())
                .stream
                .addOnSuccessListener {
                    if (continuation.isActive) {
                        Timber.d("Downloading Image success")
                        val stream = it.stream
                        val bitmap = converter.get().inputStreamToBitmap(stream) ?: return@addOnSuccessListener continuation.resumeWithException(NullPointerException())

                        // Don't forget to close the stream
                        stream.close()
                        continuation.resume(Result.Success(bitmap))
                    } else {
                        Timber.d("Continuation is no longer active")
                    }
                }.addOnFailureListener {
                    Timber.e("Failed to download Image, cause: ${it.message}")
                    continuation.resumeWithException(it)
                }
        }
    }

    companion object {
        const val ARTICLES_DIR = "articles"
    }
}
