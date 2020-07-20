/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.secret.readit.core.data.utils.wrapInCoroutineCancellable
import com.secret.readit.core.di.IoDispatcher
import com.secret.readit.core.result.Result
import com.secret.readit.model.Article
import com.secret.readit.model.articleId
import com.secret.readit.model.publisherId
import kotlinx.coroutines.CoroutineDispatcher
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Our ArticlesDataSource has one responsibility, interact directly with firebase to get/set data
 */
class DefaultArticlesDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val normalizeHelper: NormalizeHelper = NormalizeHelper()
) : ArticlesDataSource {

    override suspend fun getArticles(
        limit: Int,
        numOfAppreciation: Int,
        containCategories: List<String>,
        numOfMinutesRead: Int,
        pubIds: List<publisherId>
    ): Result<List<Article>> {
        return fetchArticles(limit, numOfAppreciation, containCategories, numOfMinutesRead, pubIds)
    }

    override suspend fun getArticle(id: articleId): Result<Article> {
        return fetchArticle(id)
    }

    override suspend fun addArticle(article: Article): Result<Boolean> {
        return addNewArticle(article)
    }

    private suspend fun fetchArticles(
        limit: Int,
        numOfAppreciation: Int,
        categoriesIds: List<String>,
        numOfMinutesRead: Int,
        pubIds: List<publisherId>
    ): Result<List<Article>> {
        return wrapInCoroutineCancellable(
            ioDispatcher
        ) { continuation ->
            // TODO:try configure the number of limit with Remote config
            // or try some pagination to avoid wasting resources
            firestore.collection(ARTICLES_COLLECTION)
                .whereGreaterThanOrEqualTo(NUM_OF_APPRECIATE_FIELD, numOfAppreciation)
                .whereGreaterThanOrEqualTo(NUM_MINUTES_READ_FIELD, numOfMinutesRead)
                .categoryOrPublishersQuery(categoriesIds, pubIds)
                .limit(limit.toLong())
                .get()
                .addOnSuccessListener { articlesSnapshot ->
                    if (continuation.isActive) {
                        Timber.d("fetched articles Successfully: ${articlesSnapshot.documents}")

                        val articles = normalizeHelper.getNormalizedArticles(articlesSnapshot)

                        continuation.resume(Result.Success(articles))
                    } else {
                        Timber.d("Exception, continuation is no longer active")
                    }
                }.addOnFailureListener {
                    Timber.d("Exception in fetching articles, Cause: ${it.message}")
                    continuation.resumeWithException(it)
                }
        }
    }

    private suspend fun fetchArticle(id: articleId): Result<Article> {
        return wrapInCoroutineCancellable(
            ioDispatcher
        ) { continuation ->
            firestore.collection(ARTICLES_COLLECTION)
                .document(id)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (continuation.isActive) {
                        Timber.d("fetched article Successfully: ${documentSnapshot.data}")

                        val article = normalizeHelper.getNormalizedArticle(documentSnapshot)

                        if (article == null) {
                            Timber.d("There's No Article with this id")
                            return@addOnSuccessListener continuation.resumeWithException(
                                NullPointerException()
                            )
                        }

                        continuation.resume(Result.Success(article))
                    } else {
                        Timber.d("Exception, continuation is no longer active")
                    }
                }.addOnFailureListener {
                    Timber.d("Exception in fetching article, Cause: ${it.message}")
                    continuation.resumeWithException(it)
                }
        }
    }

    private suspend fun addNewArticle(article: Article): Result<Boolean> {
        return wrapInCoroutineCancellable(
            ioDispatcher
        ) { continuation ->
            firestore.collection(ARTICLES_COLLECTION)
                .add(article)
                .addOnSuccessListener { reference ->
                    if (continuation.isActive) {
                        Timber.d("added article Successfully, article id: ${reference.id}")

                        continuation.resume(Result.Success(true))
                    } else {
                        Timber.d("Exception, continuation is no longer active")
                    }
                }.addOnFailureListener {
                    Timber.d("Failed to add article, cause: ${it.cause}")
                    continuation.resumeWithException(it)
                }
        }
    }

    //We Cannot get query based on publishers and category at the same query, So we need to choose between them
    private fun Query.categoryOrPublishersQuery(categoriesIds: List<String>, pubIds: List<publisherId>): Query{
        return if (!categoriesIds.isNullOrEmpty()) {
            whereArrayContainsAny(CATEGORIES_FIELD, categoriesIds)
        } else if (!pubIds.isNullOrEmpty()) {
            whereIn(PUBLISHER_ID_FILED, pubIds)
        } else {
            // This query is always true with all articles but we must have an else branch here if lists is empty, So this is the safest query to return
            whereGreaterThanOrEqualTo(TIMESTAMP_FIELD, 1)
        }
    }
    companion object {
        const val ARTICLES_COLLECTION = "articles"
        const val NUM_OF_APPRECIATE_FIELD = "numOfAppreciate"
        const val NUM_MINUTES_READ_FIELD = "numMinutesRead"
        const val CATEGORIES_FIELD = "category"
        const val TIMESTAMP_FIELD = "timestamp"
        const val PUBLISHER_ID_FILED = "publisher"
    }
}
