/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.domain.articledetail

import com.secret.readit.core.data.articles.ArticlesRepository
import com.secret.readit.core.domain.FlowUseCase
import com.secret.readit.core.uimodels.UiComment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.filterNot
import javax.inject.Inject

/**
 * UseCase that loads replies of comment, it takes a comment [UiComment] as a parameter
 * @return the replies of this comment
 */
class GetCommentReplies @Inject constructor(private val articlesRepo: ArticlesRepository): FlowUseCase<UiComment, UiComment>(){
    override suspend fun execute(parameters: UiComment): Flow<UiComment> {
        val currentArticleId = articlesRepo.currentArticleID ?: throw NullPointerException()
        return articlesRepo.showReplies(currentArticleId, parameters, 0 /*For now there's no limit*/).replies
            .sort()
            .asFlow()
            .filterNot { it.comment.publisherId.isEmpty() || it.comment.id.isEmpty()}
            .cancellable()
    }
}