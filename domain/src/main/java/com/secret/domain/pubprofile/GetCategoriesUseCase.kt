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
import com.secret.readit.core.data.categories.CategoryRepository
import com.secret.readit.core.uimodels.UiPublisher
import com.secret.readit.model.Category
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Similar to [GetFollowingUseCase] but for getting Categories,
 * **Note**: This should be cached in appropriate scope like: viewModelScope
 */
class GetCategoriesUseCase @Inject constructor(
    @CurrentUserProfile private val currentUser: UseCase<Unit, UiPublisher>,
    private val categoryRepo: CategoryRepository
) : FlowUseCase<Unit, PagingData<Category>>() {

    override suspend fun execute(parameters: Unit): Flow<PagingData<Category>> {
        val categoriesIds = currentUser(parameters).publisher.followedCategoriesIds
        return categoryRepo.getCategories(limit = LIMIT, ids = categoriesIds).map { dropEmpty(it) }
    }

    private fun dropEmpty(page: PagingData<Category>) = page.filter { it.id.isNotEmpty() && it.name.isNotEmpty() }

    companion object {
        const val LIMIT = 50 //Configured through RemoteConfig
    }
}
