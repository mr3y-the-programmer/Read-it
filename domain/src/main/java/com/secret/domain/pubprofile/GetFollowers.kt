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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * GetFollowers Usecase which load followers list
 */
// Note: This is unTested because its siblings tested well, You can add to parametrized test later if you want
class GetFollowers @Inject constructor(
    @CurrentUserProfile private val currentUser: UseCase<Unit, UiPublisher>,
    private val pubRepo: PublisherRepository
) : FlowUseCase<Unit, PagingData<UiPublisher>>() {

    override suspend fun execute(parameters: Unit): Flow<PagingData<UiPublisher>> {
        val followersIds = currentUser(parameters).publisher.followersIds
        return pubRepo.getPubs(followersIds).map {
            it.filter { pub ->
                pub.publisher.id.isNotEmpty() && pub.publisher.memberSince > 0
            }
        }
    }
}
