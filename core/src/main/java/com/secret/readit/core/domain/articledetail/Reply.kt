/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.domain.articledetail

import com.secret.readit.core.data.articles.ArticlesRepository
import com.secret.readit.core.di.CurrentUserProfile
import com.secret.readit.core.domain.FlowUseCase
import com.secret.readit.core.domain.UseCase
import com.secret.readit.core.uimodels.UiComment
import com.secret.readit.core.uimodels.UiPublisher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.NullPointerException
import javax.inject.Inject

/**
 * Reply UseCase, it takes Pair of reply and ParentComment respectively
 * @return true(on Success) or false Use these values to handle the ui
 */
class Reply @Inject constructor(private val articlesRepo: ArticlesRepository,
                                @CurrentUserProfile private val currentUser: UseCase<Unit, UiPublisher>): FlowUseCase<Pair<UiComment, UiComment>, Boolean>(){
    override suspend fun execute(parameters: Pair<UiComment, UiComment>): Flow<Boolean> {
        val currentArticleID = articlesRepo.currentArticleID ?: throw NullPointerException()
        val reply = parameters.first.copy(pub = currentUser(Unit))
        return flow { emit(articlesRepo.reply(currentArticleID, reply = reply, parentComment = parameters.second)) }
    }
}