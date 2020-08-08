/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.domain.publisharticle

import com.secret.readit.core.data.articles.ArticlesRepository
import com.secret.readit.core.data.publisher.PublisherRepository
import com.secret.readit.core.domain.FlowUseCase
import com.secret.readit.core.uimodels.UiArticle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Publish Article UseCase which called to publish article to public
 * it returns true(On success) or false which can be observed by UI consumers to change Ui state like displaying progressbar
 */
class Publish @Inject constructor(private val articlesRepo: ArticlesRepository,
                                  private val pubRepo: PublisherRepository): FlowUseCase<UiArticle, Boolean>() {
    override suspend fun execute(parameters: UiArticle): Flow<Boolean> {
        return flow {
            val addArticle = articlesRepo.addArticle(parameters)
            val incrementPubArticles = pubRepo.addNewArticle(parameters)
            emit(addArticle && incrementPubArticles)
        }
    }
}