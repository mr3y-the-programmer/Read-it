/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.domain.articledetail

import com.secret.readit.core.data.articles.ArticlesRepository
import com.secret.readit.core.domain.FlowUseCase
import com.secret.readit.core.uimodels.UiArticle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * UseCase that is responsible for Reacting to article(Appreciate, disagree), it takes a pair of:
 * -Article to which user react
 * -Reaction itself, Use one of enum [Reaction] values
 * @return true(on Success) or false You can collect these values to handle the UI gracefully
 *
 * **NOTE**: (Commenting or replying doesn't belong to this UseCase), see [AddComment], [Reply] instead
 */
class React @Inject constructor(private val articlesRepo: ArticlesRepository) : FlowUseCase<Pair<UiArticle, Reaction>, Boolean>() {
    override suspend fun execute(parameters: Pair<UiArticle, Reaction>): Flow<Boolean> {
        return flow {
            when (parameters.second) {
                Reaction.APPRECIATE -> emit(articlesRepo.appreciate(parameters.first))
                Reaction.DISAGREE -> emit(articlesRepo.disagree(parameters.first))
            }
        }
    }
}

enum class Reaction {
    APPRECIATE,
    DISAGREE
}
