/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.utils

import com.google.firebase.firestore.Query
import com.secret.readit.core.result.Result
import com.secret.readit.core.uimodels.ImageUiElement
import com.secret.readit.model.BaseElement
import com.secret.readit.model.Element
import com.secret.readit.model.Publisher
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

// firebase doesn't support coroutines yet, so we use suspendCancellableCoroutine
suspend inline fun <T> wrapInCoroutineCancellable(
    dispatcher: CoroutineDispatcher,
    crossinline block: (continuation: CancellableContinuation<Result<T>>) -> Unit
): Result<T> {
    return withContext(dispatcher) {
        suspendCancellableCoroutine<Result<T>> {
            block(it)
        }
    }
}

val BaseElement.isTextElement
    get() = this is Element && this.imageUri == null

val BaseElement.isImageElement
    get() = this is ImageUiElement && this.bitmap != null

/**
 * return query in which each item id equal to one of provided [ids]
 * [field]: the field to search in for values like: ID_FIELD
 */
fun Query.withIds(ids: List<String>, field: String) = if (!ids.isNullOrEmpty()) whereIn(field, ids) else this // Otherwise return query without additional filters

// Refactored thumbnail to be an extension property, it help our model code to be more clean
val Publisher.thumbnail
    get() = profileImgUri // TODO: this is fake implementation, real one comes later when needed
