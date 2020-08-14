/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.paging.articles

import com.secret.readit.core.paging.BaseReqParams
import com.secret.readit.model.articleId
import com.secret.readit.model.publisherId

/**
 * take parameters that customize the query/result returned from data source,
 *
 * also it takes [contentLimit] as a parameter to download limited num of content to de displayed like a summery
 */
data class RequestParams(
    val limit: Int,
    val appreciateNum: Int,
    val categoriesIds: List<String>,
    val withMinutesRead: Int,
    val mostFollowedPubsId: List<publisherId>,
    val specificPub: Pair<publisherId, Long>,
    val articleIds: List<articleId>,
    val contentLimit: Int
) : BaseReqParams()
