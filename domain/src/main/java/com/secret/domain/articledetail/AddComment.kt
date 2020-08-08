/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.domain.articledetail

import com.secret.domain.FlowUseCase
import com.secret.domain.UseCase
import com.secret.domain.di.CurrentUserProfile
import com.secret.readit.core.data.articles.ArticlesRepository
import com.secret.readit.core.uimodels.UiComment
import com.secret.readit.core.uimodels.UiPublisher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * AddComment UseCase which adds a new Comment to article,
 * it takes the comment [UiComment] as a parameter and
 * @return true(on Success) or false so you can handle the ui based on these values
 *
 * **NOTE**: the only needed attribute from Ui in param [UiComment] is actual text of comment,UI consumers don't have to care about other attributes
 */
class AddComment @Inject constructor(
    private val articlesRepo: ArticlesRepository,
    @CurrentUserProfile private val currentUser: UseCase<Unit, UiPublisher>
) : FlowUseCase<UiComment, Boolean>() {
    override suspend fun execute(parameters: UiComment): Flow<Boolean> {
        val comment = parameters.copy(pub = currentUser(Unit))
        return flow { emit(articlesRepo.comment(currentArtID(articlesRepo), comment)) }
    }
}
