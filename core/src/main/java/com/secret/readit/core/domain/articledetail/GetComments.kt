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
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * A Use Case for loading comments for specific article,
 * it takes an Int param to limit the comments number, if this limit is 0 or negative the whole comments will be loaded
 *
 * @return flow of comments
 */
class GetComments @Inject constructor(private val articlesRepo: ArticlesRepository): FlowUseCase<Int, UiComment>(){

    override suspend fun execute(parameters: Int): Flow<UiComment> {
        val currentArticleId = articlesRepo.currentArticleID ?: throw NullPointerException()
        return articlesRepo.getComments(currentArticleId, parameters)
            .sort()
            .asFlow()
            .filterNot { it.comment.publisherId.isEmpty() || it.comment.id.isEmpty()}
            .cancellable()
    }
}