/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.domain.pubprofile

import androidx.paging.PagingData
import androidx.paging.filter
import com.secret.domain.FlowUseCase
import com.secret.domain.UseCase
import com.secret.domain.di.CurrentUserProfile
import com.secret.readit.core.data.publisher.PublisherRepository
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
) : FlowUseCase<Unit, PagingData<UiPublisher>>() {

    override suspend fun execute(parameters: Unit): Flow<PagingData<UiPublisher>> {
        val followingIds = currentUser(parameters).publisher.followedPublishersIds // If it throws NullPointerException it will be caught by catch in [FlowUseCase]
        return pubRepo.getPubs(followingIds).map {
            it.filter { pub ->
                pub.publisher.id.isNotEmpty() && pub.publisher.memberSince > 0
            }
        }
    }
}
