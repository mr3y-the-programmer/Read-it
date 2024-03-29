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
import com.secret.domain.di.MostFollowedPublishers
import com.secret.readit.core.data.articles.ArticlesRepository
import com.secret.readit.core.remoteconfig.RemoteConfigSource
import com.secret.readit.core.uimodels.UiArticle
import com.secret.readit.model.publisherId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.Random
import javax.inject.Inject

/**
 * Picked up for you Use case, which takes a limit [Int] and return semi-randomly customized articles for user
 * based on some factors like: most appreciated, most-followed publishers(composite index), (short articles & appreciated a lot)
 * **NOTE**: This should be cached in appropriate scope like viewModelScope
 */

class PickedUpForYou @Inject constructor(
    private val articlesRepo: ArticlesRepository,
    @MostFollowedPublishers private val mostFollowedPubs: UseCase<Pair<Int, Int>, List<publisherId>>,
    private val remoteConfig: RemoteConfigSource
) : FlowUseCase<Int, PagingData<UiArticle>>() {

    override suspend fun execute(parameters: Int): Flow<PagingData<UiArticle>> {
        val minimum = remoteConfig.minimumArticlesLimit.value.toInt()
        val maximum = remoteConfig.maximumArticlesLimit.value.toInt()
        val limit = parameters.coerceIn(minimum, maximum) // Ensure we don't request big number of articles that user will never read
        val (mostAppreciateLimit, mostFollowedPubLimit, shortArticlesLimit) = getEachPartLimit(limit)
        // First
        val mostAppreciated = articlesRepo.getMostAppreciatedArticles(limit = mostAppreciateLimit, appreciateNum = APPRECIATE_NUMBER)
        // Second
        val pubIds = mostFollowedPubs(Pair(NUMBER_OF_FOLLOWERS, mostFollowedPubLimit))
        val mostFollowedPublishers = articlesRepo.getMostFollowedPublishersArticles(limit = mostFollowedPubLimit, pubsIds = pubIds)
        // Third
        val shortAppreciatedArticles = articlesRepo.getShortAndAppreciatedArticles(
            limit = shortArticlesLimit,
            maximumMinutesRead = MINUTES_READ_NUMBER,
            appreciateNum = SHORT_ARTICLES_APPRECIATE_NUMBER
        )
        // Wrap and combine them
        val articles = mostAppreciated.combine(mostFollowedPublishers) { fromMA, fromMFP ->
            if (Random().nextInt(2) == 0) fromMA else fromMFP
        }.combine(shortAppreciatedArticles) { other, fromSAA ->
            if (Random().nextInt(2) == 0) other else fromSAA
        }.map { dropEmptyArticles(it) }
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

    private val NUMBER_OF_FOLLOWERS = remoteConfig.withNumOfFollowers.value.toInt()
    private val APPRECIATE_NUMBER = remoteConfig.withAppreciateNum.value.toInt()
    private val MINUTES_READ_NUMBER = remoteConfig.withMinutesRead.value.toInt()
    private val SHORT_ARTICLES_APPRECIATE_NUMBER = remoteConfig.shortArtWithAppreciateNum.value.toInt()
}
