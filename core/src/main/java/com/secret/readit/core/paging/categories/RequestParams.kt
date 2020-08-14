/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.paging.categories

import com.secret.readit.core.paging.BaseReqParams

/**
 * Request Params for Category PagingSource
 */
data class RequestParams(
    val limit: Int,
    val ids: List<String>
) : BaseReqParams()
