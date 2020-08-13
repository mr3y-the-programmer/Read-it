/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.paging.publisher

import com.secret.readit.core.paging.BaseReqParams
import com.secret.readit.model.publisherId

/**
 * take parameters to customize the dataSource result
 */
data class RequestParams(val limit: Int,
                         val followersNum: Int,
                         val withIds: List<publisherId>): BaseReqParams()