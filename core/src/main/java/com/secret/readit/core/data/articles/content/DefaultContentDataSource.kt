/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles.content

import com.google.firebase.firestore.FirebaseFirestore
import com.secret.readit.core.data.utils.wrapInCoroutineCancellable
import com.secret.readit.core.di.IoDispatcher
import com.secret.readit.core.result.Result
import com.secret.readit.model.Element
import com.secret.readit.model.articleId
import kotlinx.coroutines.CoroutineDispatcher
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Our ContentDataSource has one responsibility, interact directly with firebase to get/set content of article
 */
internal class DefaultContentDataSource @Inject constructor(private val firestore: FirebaseFirestore,
                                                            @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
                                                            private val normalizer: ContentNormalizeHelper = ContentNormalizeHelper()
): ContentDataSource {
    override suspend fun getContent(id: articleId, limit: Int): Result<List<Element>> {
        return wrapInCoroutineCancellable(ioDispatcher) { continuation ->
            val safeLimit = if (limit <= 0) 9999 else limit
            firestore.collection(ARTICLES_COLLECTION)
                .document(id)
                .collection(CONTENT_COLLECTION)
                .limit(safeLimit.toLong())
                .get()
                .addOnSuccessListener { contentSnapshot ->
                    if (continuation.isActive) {
                        Timber.d("fetched content of article: $id Successfully, content: ${contentSnapshot.documents}")

                        val content = normalizer.normalizeToElements(contentSnapshot)

                        continuation.resume(Result.Success(content))
                    } else {
                        Timber.d("Exception, continuation is no longer active")
                    }
                }.addOnFailureListener {
                    Timber.d("Exception in fetching content, Cause: ${it.message}")
                    continuation.resumeWithException(it)
                }
        }
    }

    override suspend fun addContent(id: articleId, elements: List<Element>): Result<Boolean> {
        return wrapInCoroutineCancellable(ioDispatcher) { continuation ->
            val deNormalizedElements = normalizer.deNormalizeElements(elements)
            firestore.collection(ARTICLES_COLLECTION)
                .document(id)
                .collection(CONTENT_COLLECTION)
                .add(deNormalizedElements)
                .addOnSuccessListener {
                    if (continuation.isActive) {
                        Timber.d("Added content of article: $id Successfully")
                        continuation.resume(Result.Success(true))
                    } else {
                        Timber.d("Exception, continuation is no longer active")
                    }
                }.addOnFailureListener {
                    Timber.d("Exception in adding article: $id content, Cause: ${it.message}")
                    continuation.resumeWithException(it)
                }
        }
    }

    companion object {
        const val ARTICLES_COLLECTION = "articles"
        const val CONTENT_COLLECTION = "content"
    }
}