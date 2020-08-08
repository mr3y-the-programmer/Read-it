/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.domain.pubprofile

import com.secret.readit.core.data.publisher.PublisherRepository
import com.secret.domain.FlowUseCase
import com.secret.readit.core.uimodels.UiPublisher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Identical to [UpdateCategories] but for Following publishers
 */
class UpdateFollowing @Inject constructor(private val pubRepo: PublisherRepository): FlowUseCase<Pair<UiPublisher, UpdateFollow>, Boolean>() {
    override suspend fun execute(parameters: Pair<UiPublisher, UpdateFollow>): Flow<Boolean> {
        return flow {
            when(parameters.second) {
                UpdateFollow.FOLLOW -> emit(pubRepo.followPublisher(parameters.first))
                UpdateFollow.UNFOLLOW -> emit(pubRepo.unFollowPublisher(parameters.first))
            }
        }
    }
}

enum class UpdateFollow{
    FOLLOW,
    UNFOLLOW
}