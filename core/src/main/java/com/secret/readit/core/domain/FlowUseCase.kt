/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import timber.log.Timber

/**
 * the Flow version of [UseCase]
 */
abstract class FlowUseCase<in P, out R> {
    /**
     * Executes the FlowUseCase asynchronously
     */
    suspend operator fun invoke(parameters: P): Flow<R> {
        return execute(parameters)
            // Catch exceptions if there's any, Consumers should handle this like: navigating to Sign-In dialog
            .catch { e -> Timber.e("Exception happened while executing, cause: ${e.message}"); throw e }
    }

    /**
     * This is should be overridden by children to execute their special Flow case
     */
    protected abstract suspend fun execute(parameters: P): Flow<R>
}
