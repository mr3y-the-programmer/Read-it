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
import com.secret.readit.model.Article
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Identical to [FromPublishersYouFollow] but for categories
 */
class BasedOnYourCategories @Inject constructor(private val pubRepo: PublisherRepository,
                                                private val articlesRepo: ArticlesRepository): FlowUseCase<Int, Article>() {
    override suspend fun execute(parameters: Int): Flow<Article> {
        val limit = parameters.coerceIn(5, 30) // Ensure we don't request big number of articles that user will never read
        val categoryFollowedIds = pubRepo.getCurrentUser().publisher.followedCategoriesIds

        return articlesRepo.getArticlesWhichHaveCategories(limit, categoryFollowedIds).asFlow()
            .filterNot { it.id.isEmpty() || it.timestamp < 0 }
            .cancellable() // asFlow is unSafeFlow so we need to check the cancellation by using cancellable()
    }
}