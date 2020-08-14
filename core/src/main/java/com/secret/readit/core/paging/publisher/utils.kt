/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.paging.publisher

import androidx.paging.PagingSource
import com.google.firebase.firestore.DocumentSnapshot
import com.secret.readit.core.paging.checkIfSuccessful
import com.secret.readit.core.result.Result

fun <T : Any> process(
    result: Result<Pair<List<T>, DocumentSnapshot>>,
    params: PagingSource.LoadParams<DocumentSnapshot>
): PagingSource.LoadResult<DocumentSnapshot, T> {
    var lastSnapshot = params.key
    val data = checkIfSuccessful(result)?.let {
        lastSnapshot = it.second
        it.first
    } ?: return PagingSource.LoadResult.Error(Exception())
    return PagingSource.LoadResult.Page(data, null /*We don't support paging in that direction*/, nextKey = lastSnapshot)
}
