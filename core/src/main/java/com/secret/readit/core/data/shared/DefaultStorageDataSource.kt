/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.shared

import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.secret.readit.core.data.utils.wrapInCoroutineCancellable
import com.secret.readit.core.di.IoDispatcher
import com.secret.readit.core.prefs.SharedPrefs
import com.secret.readit.core.result.Result
import dagger.Lazy
import kotlinx.coroutines.CoroutineDispatcher
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Our DefaultStorageDataSource has one responsibility, interact directly with storage to upload/download images
 */
internal class DefaultStorageDataSource @Inject constructor(
    private val storage: FirebaseStorage,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val converter: Lazy<Converter>,
    private val prefs: SharedPrefs
) : StorageDataSource {

    /**
     * upload img with this path and return a uri that can be used to download it later
     */
    override suspend fun uploadBitmap(
        id: String,
        imgPath: String,
        des: Destination
    ): Result<Uri> {
        return wrapInCoroutineCancellable(ioDispatcher) { continuation ->
            val root = storage.reference // root reference
            val ref = when (des) {
                Destination.ARTICLES -> root.child(ARTICLES_DIR).child(id)
                Destination.PUBLISHER -> root.child(PUBLISHERS_DIR).child(id)
            }
            val storageFile = uploadFile(imgPath, ref)
            storageFile.addOnProgressListener {
                it.uploadSessionUri.let { uri -> if (uri != null) prefs.updateUploadUri(uri.toString()) }
            }
            storageFile.continueWithTask {
                if (it.isSuccessful) {
                    ref.downloadUrl
                } else {
                    Timber.e("task isn't successful, cause: ${it.exception?.message}")
                    throw it.exception?.cause!!
                }
            }.addOnSuccessListener {
                if (continuation.isActive) {
                    Timber.d("Uploading Image success")
                    prefs.updateUploadUri("") // Don't forget to reset current Uri
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

    private fun uploadFile(filePath: String, ref: StorageReference): UploadTask {
        val imgUri = converter.get().pathToUri(filePath)
        val existingUri = prefs.currentUploadSessionUri.value
        return if (existingUri.isNotEmpty()) ref.putFile(imgUri, StorageMetadata.Builder().build(), existingUri.toUri()) else ref.putFile(imgUri)
    }

    companion object {
        const val ARTICLES_DIR = "articles"
        const val PUBLISHERS_DIR = "publishers"
    }
}
