/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.domain

import kotlinx.coroutines.flow.*
import timber.log.Timber

/**
 * the Flow version of [UseCase]
 */
abstract class FlowUseCase<in P, out R> {
    /**
     * Executes the FlowUseCase asynchronously
     */
    suspend operator fun invoke(parameters: P): Flow<R> {
        // Catch exceptions if there's any, and in this case return emptyFlow()
        // So, Consumers can handle this as they need like: opening a Sign-In dialog...etc
        return try {
            execute(parameters)
        } catch (ex: Exception) {
            Timber.e("Exception happened while executing, cause: ${ex.message}");
            emptyFlow()
        } /* **NOTE** This is a temporary solution until we can use Flow.catch() operator again*/
    }

    /**
     * This is should be overridden by children to execute their special Flow case
     */
    protected abstract suspend fun execute(parameters: P): Flow<R>
}
