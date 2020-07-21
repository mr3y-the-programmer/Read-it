/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.domain.articles

import com.secret.readit.core.data.publisher.PublisherRepository
import com.secret.readit.core.domain.UseCase
import com.secret.readit.model.publisherId
import javax.inject.Inject

/**
 * Since articles and publishers are two separate top-level collections and there is no direct relation between them
 * getting articles based on most-followed publishers can be done with several solutions but it is a pain & tricky!!!
 * And moreover there's solutions that would break architecture,
 *
 * So This UseCase would follow the easiest solution we found to be ready for this query without breaking architecture
 */
class MostFollowedPublishersArticles @Inject constructor(private val pubRepository: PublisherRepository) : UseCase<Pair<Int, Int>, List<publisherId>>() {

    /**
     * takes a pair of numberOfFollowers and limit respectively
     */
    override suspend fun execute(parameters: Pair<Int, Int>): List<publisherId> {
        val publishers = pubRepository.getPublishersWithNumberOfFollowers(parameters.first, parameters.second)
        val pubIds = mutableListOf<publisherId>()
        for (pub in publishers) {
            pubIds.add(pub.id)
        }
        return pubIds
    }
}
