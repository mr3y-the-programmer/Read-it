/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.domain.pubprofile

import com.secret.readit.core.data.publisher.PublisherRepository
import com.secret.readit.core.di.CurrentUserProfile
import com.secret.readit.core.domain.FlowUseCase
import com.secret.readit.core.domain.UseCase
import com.secret.readit.core.uimodels.UiPublisher
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * get the followed publishers UseCase which loads the following pub list
 *
 * **NOTE**: this is only should be visible to current user, other users shouldn't see who you're following
 */
class GetFollowingUseCase @Inject constructor(
    @CurrentUserProfile private val currentUser: UseCase<Unit, UiPublisher>,
    private val pubRepo: PublisherRepository
) : FlowUseCase<Unit, UiPublisher>() {

    override suspend fun execute(parameters: Unit): Flow<UiPublisher> {
        val followingIds = currentUser(parameters).publisher.followedPublishersIds // If it throws NullPointerException it will be caught by catch in [FlowUseCase]
        return pubRepo.getFollowingPubsList(followingIds).asFlow()
            .filterNot { it.publisher.id.isEmpty() || it.publisher.memberSince < 0 }
    }
}
