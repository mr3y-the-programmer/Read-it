/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles.utils

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import com.secret.readit.core.result.Result

// firebase doesn't support coroutines yet, so we use suspendCancellableCoroutine
suspend fun <T> wrapInCoroutineCancellable(dispatcher: CoroutineDispatcher,
                                           block: (continuation: CancellableContinuation<Result<T>>) -> Unit): Result<T>{
    return withContext(dispatcher){
        suspendCancellableCoroutine<Result<T>> {
            block(it)
        }
    }
}