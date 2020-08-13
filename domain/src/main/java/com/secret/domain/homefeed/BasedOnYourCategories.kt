/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.domain.homefeed

import androidx.paging.PagingData
import com.secret.domain.FlowUseCase
import com.secret.domain.UseCase
import com.secret.domain.di.CurrentUserProfile
import com.secret.readit.core.data.articles.ArticlesRepository
import com.secret.readit.core.uimodels.UiArticle
import com.secret.readit.core.uimodels.UiPublisher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Identical to [FromPublishersYouFollow] but for categories
 * **NOTE**: This should be cached in appropriate scope like viewModelScope
 */
class BasedOnYourCategories @Inject constructor(
    @CurrentUserProfile private val currentUser: UseCase<Unit, UiPublisher>,
    private val articlesRepo: ArticlesRepository
) : FlowUseCase<Int, PagingData<UiArticle>>() {
    override suspend fun execute(parameters: Int): Flow<PagingData<UiArticle>> {
        val limit = parameters.coerceIn(5, 30) // Ensure we don't request big number of articles that user will never read
        val categoryFollowedIds = currentUser(Unit).publisher.followedCategoriesIds

        return articlesRepo.getArticlesWhichHaveCategories(limit, categoryFollowedIds).map {
            dropEmptyArticles(it)
        }
    }
}
