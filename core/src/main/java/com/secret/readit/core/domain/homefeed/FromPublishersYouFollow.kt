/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.domain.homefeed

import com.secret.readit.core.data.articles.ArticlesRepository
import com.secret.readit.core.data.publisher.PublisherRepository
import com.secret.readit.core.domain.FlowUseCase
import com.secret.readit.core.uimodels.UiArticle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.filterNot
import javax.inject.Inject

/**
 * get Articles From Publishers You follow UseCase, takes a limit [Int]
 * and return Flow of articles
 */
class FromPublishersYouFollow @Inject constructor(
    private val pubRepo: PublisherRepository,
    private val articlesRepo: ArticlesRepository
) : FlowUseCase<Int, UiArticle>() {

    override suspend fun execute(parameters: Int): Flow<UiArticle> {
        val limit = parameters.coerceIn(5, 30) // Ensure we don't request big number of articles that user will never read
        val followingIds = pubRepo.getCurrentUser().publisher.followedPublishersIds

        // asFlow is unSafeFlow so we need to check the cancellation by using cancellable()
        return articlesRepo.getMostFollowedPublishersArticles(limit, followingIds).asFlow()
            .filterNot { it.article.id.isEmpty() || it.article.timestamp < 0 }
            .cancellable()
    }
}
