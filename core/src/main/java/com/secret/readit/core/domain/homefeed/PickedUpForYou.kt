/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.domain.homefeed

import com.secret.readit.core.data.articles.ArticlesRepository
import com.secret.readit.core.di.MostFollowedPublishers
import com.secret.readit.core.domain.FlowUseCase
import com.secret.readit.core.domain.UseCase
import com.secret.readit.model.Article
import com.secret.readit.model.publisherId
import kotlinx.coroutines.flow.*
import java.util.Random
import javax.inject.Inject

/**
 * Picked up for you Use case, which takes a limit [Int] and return semi-randomly customized articles for user
 * based on some factors like: most appreciated, most-followed publishers(composite index), (short articles & appreciated a lot)
 */
class PickedUpForYou @Inject constructor(
    private val articlesRepo: ArticlesRepository,
    @MostFollowedPublishers private val mostFollowedPubs: UseCase<Pair<Int, Int>, List<publisherId>>
) : FlowUseCase<Int, Article>() {

    override suspend fun execute(parameters: Int): Flow<Article> {
        val limit = parameters.coerceIn(5, 30) // Ensure we don't request big number of articles that user will never read
        val (mostAppreciateLimit, mostFollowedPubLimit, shortArticlesLimit) = getEachPartLimit(limit)
        // First
        val mostAppreciated = articlesRepo.getMostAppreciatedArticles(limit = mostAppreciateLimit, appreciateNum = APPRECIATE_NUMBER).asFlow()
        // Second
        val pubIds = mostFollowedPubs(Pair(NUMBER_OF_FOLLOWERS, mostFollowedPubLimit))
        val mostFollowedPublishers = articlesRepo.getMostFollowedPublishersArticles(limit = mostFollowedPubLimit, pubsIds = pubIds).asFlow()
        // Third
        val shortAppreciatedArticles = articlesRepo.getShortAndAppreciatedArticles(
            limit = shortArticlesLimit,
            maximumMinutesRead = MINUTES_READ_NUMBER,
            appreciateNum = SHORT_ARTICLES_APPRECIATE_NUMBER
        ).asFlow()
        // Wrap and combine them
        val articles = mostAppreciated.combine(mostFollowedPublishers) { fromMA, fromMFP ->
            if (Random().nextInt(2) == 0) fromMA else fromMFP
        }.combine(shortAppreciatedArticles) { other, fromSAA ->
            if (Random().nextInt(2) == 0) other else fromSAA
        }.filterNot { it.id.isEmpty() || it.timestamp < 0 }.cancellable() // cancellable() because asFlow() is unSafeFlow
        return articles
    }

    /**
     * divide original limit into small limits
     */
    private fun getEachPartLimit(originalLimit: Int): Triple<Int, Int, Int> {
        val random = Random()
        val oneThirdOriginalLimit = originalLimit.div(3).coerceAtLeast(3)

        val first = random.nextInt(originalLimit).coerceIn(1, oneThirdOriginalLimit)
        val second = random.nextInt(originalLimit).coerceIn(1, oneThirdOriginalLimit)
        val third = random.nextInt(originalLimit).coerceIn(1, oneThirdOriginalLimit)

        return Triple(first, second, third)
    }

    companion object {
        // TODO: update this to be configured through RemoteConfig
        const val NUMBER_OF_FOLLOWERS = 100
        const val APPRECIATE_NUMBER = 1000
        const val MINUTES_READ_NUMBER = 4
        const val SHORT_ARTICLES_APPRECIATE_NUMBER = 4000
    }
}
