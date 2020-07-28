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

class FakeCommentsDataSource : CommentDataSource {
    override suspend fun getComments(articleID: articleId, ids: List<String>, limit: Int): Result<List<Comment>> = Result.Success(TestData.comments1)

    override suspend fun addComment(articleID: articleId, comment: Comment): Result<Boolean> {
        TestData.comments1.add(TestData.newComment)
        return Result.Success(true)
    }

    override suspend fun addReply(
        articleID: articleId,
        commentId: String,
        reply: Comment
    ): Result<Boolean> {
        TestData.newComment = TestData.newComment.copy(repliesIds = listOf(TestData.newReply.id))
        return Result.Success(true)
    }
}
