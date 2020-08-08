/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.domain.pubprofile

import com.secret.readit.core.data.publisher.PublisherRepository
import com.secret.domain.FlowUseCase
import com.secret.readit.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * UpdateCategories UseCase which takes a pair of:
 * -the Category User want to subscribe/unsubscribe
 * -one of [UpdateCategory] values which specify the action done on this category
 * return true(On success) or false
 */
class UpdateCategories @Inject constructor(private val pubRepo: PublisherRepository): FlowUseCase<Pair<Category, UpdateCategory>, Boolean>(){
    override suspend fun execute(parameters: Pair<Category, UpdateCategory>): Flow<Boolean> {
        return flow {
            when(parameters.second) {
                UpdateCategory.ADD -> emit(pubRepo.followCategory(parameters.first))
                UpdateCategory.REMOVE -> emit(pubRepo.unFollowCategory(parameters.first))
            }
        }
    }
}

enum class UpdateCategory{
    ADD,
    REMOVE
}