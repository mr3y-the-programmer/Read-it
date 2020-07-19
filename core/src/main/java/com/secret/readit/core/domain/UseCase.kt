/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Using UseCase technique is powerful in domain layer to separate business logic from presenter,
 *
 * This really inspired by IOsched app, see: https://github.com/google/iosched/tree/adssched/shared/src/main/java/com/google/samples/apps/iosched/shared/domain
 */
abstract class UseCase<in P, out R>(private val dispatcher: CoroutineDispatcher) {

    /**
     * Executes the UseCase asynchronously
     */
    suspend operator fun invoke(parameters: P): R{
        return withContext(dispatcher) {
            execute(parameters)
        }
    }

    /**
     * This is should be overridden by children to execute their special case
     */
    protected abstract fun execute(parameters: P): R
}