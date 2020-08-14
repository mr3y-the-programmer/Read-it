/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.domain.pubprofile

import androidx.paging.PagingData
import com.secret.domain.FlowUseCase
import com.secret.domain.UseCase
import com.secret.domain.di.CurrentUserProfile
import com.secret.domain.homefeed.dropEmptyArticles
import com.secret.readit.core.data.articles.ArticlesRepository
import com.secret.readit.core.uimodels.UiArticle
import com.secret.readit.core.uimodels.UiPublisher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * GetBookmarks Usecase which get the articles User bookmarked them
 */
class GetBookmarks @Inject constructor(
    @CurrentUserProfile private val currentUser: UseCase<Unit, UiPublisher>,
    private val articlesRepo: ArticlesRepository
) : FlowUseCase<Unit, PagingData<UiArticle>>() {

    override suspend fun execute(parameters: Unit): Flow<PagingData<UiArticle>> {
        val pub = currentUser(parameters).publisher
        val bookmarkedArticles = pub.bookmarkedArticlesIds
        return articlesRepo.getSpecificPubArticles(pub.id, bookmarkedArticles).map {
            dropEmptyArticles(it)
        }
    }
}