/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.domain.pubprofile

import com.secret.domain.FlowUseCase
import com.secret.readit.core.data.publisher.PublisherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Update UserName UseCase
 */
class UpdateUserName @Inject constructor(private val pubRepo: PublisherRepository) : FlowUseCase<String, Boolean>() {
    override suspend fun execute(parameters: String): Flow<Boolean> = flow { emit(pubRepo.updateName(parameters)) }
}
