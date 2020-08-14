/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.paging

import com.google.firebase.firestore.DocumentSnapshot
import com.secret.readit.core.result.Result
import com.secret.readit.core.result.succeeded

fun <T> checkIfSuccessful(result: Result<Pair<List<T>, DocumentSnapshot>>) = if (result != null && result.succeeded) (result as Result.Success).data else null
