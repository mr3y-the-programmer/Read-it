/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles

import com.google.firebase.firestore.FirebaseFirestore
import com.secret.readit.core.di.IoDispatcher
import com.secret.readit.core.result.Result
import com.secret.readit.model.Article
import com.secret.readit.model.articleId
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// TODO: return to this when working with domain layer (Usecases)
/**
 * Our ArticlesDataSource has one responsibility, interact directly with firebase to get/set data
 */
class DefaultArticlesDataSource @Inject constructor(private val firestore: FirebaseFirestore,
                                                    @IoDispatcher private val ioDispatcher: CoroutineDispatcher) : ArticlesDataSource {

    override suspend fun getArticles(): Result<List<Article>> {
        return fetchArticles()
    }

    //TODO: separate common parts in fetchArticle/s in other fun
    //TODO: Inject NormalizeHelper()
    override suspend fun getArticle(id: articleId): Result<Article> {
        return fetchArticle(id)
    }

    override suspend fun bookmark(id: articleId, bookmark: Boolean): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun addArticle(article: Article): Result<Boolean> {
        return addNewArticle(article)
    }

    private suspend fun fetchArticles(): Result<List<Article>> {
        // firebase doesn't support coroutines yet, so we use suspendCancellableCoroutine
        return withContext(ioDispatcher) {
            suspendCancellableCoroutine<Result<List<Article>>> { continuation ->

                // TODO:try configure the number of limit with Remote config
                // or try some pagination to avoid wasting resources
                firestore.collection("articles")
                    .get()
                    .addOnSuccessListener { articlesSnapshot ->
                        if (continuation.isActive) {
                            Timber.d("fetched articles Successfully: ${articlesSnapshot.documents}")

                            val articles = NormalizeHelper().getNormalizedArticles(articlesSnapshot)

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
    }

    private suspend fun fetchArticle(id: articleId): Result<Article> {
        // firebase doesn't support coroutines yet, so we use suspendCancellableCoroutine
        return withContext(ioDispatcher) {
            suspendCancellableCoroutine<Result<Article>> { continuation ->

                // TODO:try configure the number of limit with Remote config
                // or try some pagination to avoid wasting resources
                firestore.collection("articles")
                    .document(id)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (continuation.isActive) {
                            Timber.d("fetched article Successfully: ${documentSnapshot.data}")

                            val article = NormalizeHelper().getNormalizedArticle(documentSnapshot)

                            if (article == null){
                                Timber.d("There's No Article with this id")
                                return@addOnSuccessListener continuation.resumeWithException(NullPointerException())
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
    }

    private suspend fun addNewArticle(article: Article): Result<Boolean> {
        // firebase doesn't support coroutines yet, so we use suspendCancellableCoroutine
        return withContext(ioDispatcher) {
            suspendCancellableCoroutine<Result<Boolean>> { continuation ->

                // TODO:try configure the number of limit with Remote config
                // or try some pagination to avoid wasting resources
                firestore.collection("articles")
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
    }
}
