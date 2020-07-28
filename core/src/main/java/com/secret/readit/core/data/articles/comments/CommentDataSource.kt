/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles.comments

import com.secret.readit.core.result.Result
import com.secret.readit.model.Comment
import com.secret.readit.model.articleId

/**
 * Blueprint of basic operations on sub-collection comments of each document in articles collection
 */
interface CommentDataSource {
    /**
     * get comments of article specified by [articleID],
     * it also takes additional parameters like [limit] to customize query
     * if limit isn't valid or specified it will return the whole comments
     * [ids] parameter for getting comments with specified ids
     */
    suspend fun getComments(articleID: articleId, ids: List<String>, limit: Int): Result<List<Comment>>

    /**
     * upload Comment to firestore specific for this [articleId]
     *
     * @return true on Success, otherwise false
     */
    suspend fun addComment(articleID: articleId, comment: Comment): Result<Boolean>

    /**
     * upload reply[reply] to comment [commentId] for specific article [articleID]
     *
     * @return true on Success, otherwise false
     */
    suspend fun addReply(articleID: articleId, commentId: String, reply: Comment): Result<Boolean>
}