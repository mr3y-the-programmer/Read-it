/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber

/**
 * the Flow version of [UseCase]
 */
abstract class FlowUseCase<in P, out R>(private val dispatcher: CoroutineDispatcher) {
    /**
     * Executes the FlowUseCase asynchronously
     */
    operator fun invoke(parameters: P): Flow<R>{
        return execute(parameters)
            .catch { e -> Timber.e("Exception happened while executing, cause: ${e.message}"); throw e}  //Catch exceptions if happened
            .flowOn(dispatcher)
    }

    /**
     * This is should be overridden by children to execute their special Flow case
     */
    protected abstract fun execute(parameters: P): Flow<R>
}