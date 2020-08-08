/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.domain.articledetail

import com.secret.domain.UseCase
import com.secret.readit.core.data.articles.ArticlesRepository
import com.secret.readit.core.uimodels.UiArticle
import javax.inject.Inject

/**
 * When clicking on article, This Use Case'll display the Full Article content
 * it takes the [UiArticle] we want to download its full content and return it
 */
class GetFullArticle @Inject constructor(private val articlesRepo: ArticlesRepository) : UseCase<UiArticle, UiArticle>() {
    override suspend fun execute(parameters: UiArticle): UiArticle = articlesRepo.getFullArticle(partialArticle = parameters)
}
