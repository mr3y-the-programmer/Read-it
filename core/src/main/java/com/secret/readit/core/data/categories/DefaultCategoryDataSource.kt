/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.categories

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.secret.readit.core.data.utils.after
import com.secret.readit.core.data.utils.withIds
import com.secret.readit.core.data.utils.wrapInCoroutineCancellable
import com.secret.readit.core.di.IoDispatcher
import com.secret.readit.core.result.Result
import com.secret.readit.model.Category
import com.secret.readit.model.articleId
import kotlinx.coroutines.CoroutineDispatcher
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Our CategoryDataSource has one responsibility, interact directly with firestore to get data
 */
internal class DefaultCategoryDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CategoryDataSource {

    /**
     * For now, all returned categories guaranteed to be non-null since it only configured/added through server not from the client
     */
    override suspend fun getCategories(limit: Int, ids: List<String>, prevSnapshot: DocumentSnapshot?): Result<Pair<List<Category>, DocumentSnapshot>> {
        return wrapInCoroutineCancellable(
            ioDispatcher
        ) { continuation ->
            firestore.collection(CATEGORIES_COLLECTION)
                .withIds(ids, ID_FIELD)
                .limit(limit.toLong())
                .after(prevSnapshot)
                .get()
                .addOnSuccessListener { categoriesSnapshot ->
                    if (continuation.isActive) {
                        Timber.d("fetching categories Success,categories returned: ${categoriesSnapshot.documents}")
                        val categories = categoriesSnapshot.toObjects(Category::class.java)
                        continuation.resume(Result.Success(Pair(categories, categoriesSnapshot.documents.last())))
                    } else {
                        Timber.d("continuation is no longer active")
                    }
                }.addOnFailureListener {
                    Timber.d("Failed to get categories, cause: ${it.cause}")
                    continuation.resumeWithException(it)
                }
        }
    }

    /**
     * For now, all returned categories guaranteed to be non-null since it only configured/added through server not from the client
     */
    override suspend fun getArticleCategories(id: articleId): Result<List<Category>> {
        return getArticleIdsCategories(id)
    }

    private suspend fun getArticleIdsCategories(id: articleId): Result<List<Category>> {
        return wrapInCoroutineCancellable(
            ioDispatcher
        ) { continuation ->
            firestore.collection(CATEGORIES_COLLECTION)
                .whereArrayContains(ARTICLE_IDS_FIELD, id)
                .get()
                .addOnSuccessListener { categoriesSnapshot ->
                    if (continuation.isActive) {
                        Timber.d("fetching article's categories Success,categories returned: ${categoriesSnapshot.documents}")
                        val categories = categoriesSnapshot.toObjects(Category::class.java)
                        continuation.resume(Result.Success(categories))
                    } else {
                        Timber.d("continuation is no longer active")
                    }
                }.addOnFailureListener {
                    Timber.d("Failed to get article's categories, cause: ${it.cause}")
                    continuation.resumeWithException(it)
                }
        }
    }

    companion object {
        const val ID_FIELD = "id"
        const val CATEGORIES_COLLECTION = "categories"
        const val ARTICLE_IDS_FIELD = "articleIds"
    }
}
