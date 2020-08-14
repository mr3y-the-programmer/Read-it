/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.domain.pubprofile

import com.secret.domain.FlowUseCase
import com.secret.readit.core.data.publisher.PublisherRepository
import com.secret.readit.core.prefs.SharedPrefs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * [UpdateUserMainInfo] UseCase which update User info like name, profileImg...etc
 *  it takes a pair of:
 *   -[UpdateMainType] value
 *   -the actual new value, it can be the new name or new Profile Img path...etc
 * return true(On success) or false
 */
@ExperimentalCoroutinesApi
class UpdateUserMainInfo @Inject constructor(
    private val pubRepo: PublisherRepository,
    private val prefs: SharedPrefs
) : FlowUseCase<Pair<UpdateMainType, String>, Boolean>() {
    override suspend fun execute(parameters: Pair<UpdateMainType, String>): Flow<Boolean> {
        return when(parameters.first) {
            UpdateMainType.NAME -> flow {
                prefs.updateUserName(parameters.second) // Update Shared Prefs value
                emit(pubRepo.updateName(parameters.second))
            }
            UpdateMainType.PROFILE_IMG -> flow { emit(pubRepo.updateProfileImg(parameters.second)) }
        }
    }
}

/**
 * Define the field to update
 */
enum class UpdateMainType{
    NAME,
    PROFILE_IMG
}
