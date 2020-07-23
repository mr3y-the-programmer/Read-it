/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.domain.pubprofile

import com.secret.readit.core.data.categories.CategoryRepository
import com.secret.readit.core.di.CurrentUserProfile
import com.secret.readit.core.domain.FlowUseCase
import com.secret.readit.core.domain.UseCase
import com.secret.readit.core.uimodels.UiPublisher
import com.secret.readit.model.Category
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Similar to [GetFollowingUseCase] but for getting Categories
 */
class GetCategoriesUseCase @Inject constructor(
    @CurrentUserProfile private val currentUser: UseCase<Unit, UiPublisher>,
    private val categoryRepo: CategoryRepository): FlowUseCase<Unit, Category>(){

    override suspend fun execute(parameters: Unit): Flow<Category> {
        val categoriesIds = currentUser(parameters).publisher.followedCategoriesIds
        return categoryRepo.getCategories(categoriesIds).asFlow()
            .filterNot { it.id.isEmpty() || it.name.isEmpty()}
            .cancellable()
    }
}