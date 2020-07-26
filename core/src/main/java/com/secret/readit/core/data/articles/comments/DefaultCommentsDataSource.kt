/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles.comments

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.secret.readit.core.data.utils.wrapInCoroutineCancellable
import com.secret.readit.core.di.IoDispatcher
import com.secret.readit.core.result.Result
import com.secret.readit.model.Comment
import com.secret.readit.model.articleId
import kotlinx.coroutines.CoroutineDispatcher
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Our CommentsDataSource has one responsibility, interact directly with firebase to get/set comments on each article
 */
internal class DefaultCommentsDataSource @Inject constructor(private val firestore: FirebaseFirestore,
                                                    @IoDispatcher private val ioDispatcher: CoroutineDispatcher): CommentDataSource {
    override suspend fun getComments(articleID: articleId, limit: Int): Result<List<Comment>> {
        return wrapInCoroutineCancellable(ioDispatcher) { continuation ->
            firestore.collection(ARTICLES_COLLECTION)
                .document(articleID)
                .collection(COMMENTS_COLLECTION)
                .limit(limit.toLong())
                .get()
                .addOnSuccessListener { commentsSnapshot ->
                    if (continuation.isActive) {
                        Timber.d("fetched comments of article: $articleID Successfully, comments: ${commentsSnapshot.documents}")

                        val comments = commentsSnapshot.toObjects(Comment::class.java)

                        continuation.resume(Result.Success(comments))
                    } else {
                        Timber.d("Exception, continuation is no longer active")
                    }
                }.addOnFailureListener {
                    Timber.d("Exception in fetching comments, Cause: ${it.message}")
                    continuation.resumeWithException(it)
                }
        }
    }

    override suspend fun addComment(articleID: articleId, comment: Comment): Result<Boolean> {
        return wrapInCoroutineCancellable(ioDispatcher) { continuation ->
            firestore.collection(ARTICLES_COLLECTION)
                .document(articleID)
                .collection(COMMENTS_COLLECTION)
                .document(comment.id)
                .set(comment, SetOptions.merge())
                .addOnSuccessListener {
                    if (continuation.isActive) {
                        Timber.d("added comment of article: $articleID Successfully")
                        continuation.resume(Result.Success(true))
                    } else {
                        Timber.d("Exception, continuation is no longer active")
                    }
                }.addOnFailureListener {
                    Timber.d("Exception in adding comment, Cause: ${it.message}")
                    continuation.resumeWithException(it)
                }
        }
    }

    override suspend fun addReply(articleID: articleId, commentId: String, reply: Comment): Result<Boolean> {
        return wrapInCoroutineCancellable(ioDispatcher) { continuation ->
            val commentDoc = firestore.collection(ARTICLES_COLLECTION).document(articleID).collection(COMMENTS_COLLECTION).document(commentId)
            val replyDoc = firestore.collection(ARTICLES_COLLECTION).document(articleID).collection(COMMENTS_COLLECTION).document(reply.id)

            firestore.runBatch { batch ->
                batch.update(commentDoc, REPLIES_FIELD, FieldValue.arrayUnion(reply.id))

                batch.set(replyDoc, reply)
            }.addOnSuccessListener {
                if (continuation.isActive) {
                    Timber.d("added reply to comment: $commentId Successfully")
                    continuation.resume(Result.Success(true))
                } else {
                    Timber.d("Exception, continuation is no longer active")
                }
            }.addOnFailureListener {
                Timber.d("Exception in adding reply to comment: $commentId, Cause: ${it.message}")
                continuation.resumeWithException(it)
            }
        }
    }

    companion object {
        const val ARTICLES_COLLECTION = "articles"
        const val COMMENTS_COLLECTION = "comments"
        const val REPLIES_FIELD = "repliesIds"
    }
}