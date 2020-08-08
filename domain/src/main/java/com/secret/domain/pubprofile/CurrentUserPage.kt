/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.domain.pubprofile

import com.secret.readit.core.data.publisher.PublisherRepository
import com.secret.domain.UseCase
import com.secret.readit.core.prefs.SharedPrefs
import com.secret.readit.core.uimodels.UiPublisher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber
import javax.inject.Inject

/**
 * Use case that holds the basic information about currently signed-in user,
 *
 * Consumers of this UseCase should ensure User is signed-in or wrap this UseCase in try/catch
 */
@ExperimentalCoroutinesApi
class CurrentUserPage @Inject constructor(
    private val pubRepo: PublisherRepository,
    private val prefs: SharedPrefs
) : UseCase<Unit, UiPublisher>() {

    override suspend fun execute(parameters: Unit): UiPublisher {
        if (!prefs.isUserLoggedIn.value) {
            Timber.d("No Signed In User, Sign-In First to see Your Profile")
            throw NullPointerException("SIGN-IN First")
        }
        return pubRepo.getCurrentUser()
    }
}
