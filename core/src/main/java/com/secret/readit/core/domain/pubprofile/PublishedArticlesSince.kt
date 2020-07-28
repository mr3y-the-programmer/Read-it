/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.domain.pubprofile

import com.secret.readit.core.data.articles.ArticlesRepository
import com.secret.readit.core.data.publisher.PubImportantInfo
import com.secret.readit.core.data.publisher.PublisherRepository
import com.secret.readit.core.domain.FlowUseCase
import com.secret.readit.core.uimodels.UiArticle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.filterNot
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

/**
 * get PublishedArticles Use Case, it takes a pair of respectively:
 *   -[PubImportantInfo] which hold the pub important info to identify his identity
 *   -One of enum [Since] values as a parameter to load the corresponding published Articles
 */
class PublishedArticlesSince @Inject constructor(
    private val pubRepo: PublisherRepository,
    private val articlesRepo: ArticlesRepository
) : FlowUseCase<Pair<PubImportantInfo, Since>, UiArticle>() {

    override suspend fun execute(parameters: Pair<PubImportantInfo, Since>): Flow<UiArticle> {
        val pubInfo = parameters.first
        val pubId = pubRepo.getPublisherId(pubInfo) ?: throw NullPointerException() // Will be caught by [FlowUseCase]
        val period = when (parameters.second) {
            Since.LAST_7_DAYS -> ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).minusDays(7).toEpochSecond()
            Since.LAST_MONTH -> ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).minusDays(30).toEpochSecond()
            Since.OLDER -> pubInfo.memberSince
        }
        return articlesRepo.getPubArticlesSince(pubId, period).asFlow()
            .filterNot { it.article.id.isEmpty() || it.article.timestamp < 0 }
            .cancellable()
    }
}

enum class Since {
    LAST_7_DAYS,
    LAST_MONTH,
    OLDER
}
