/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.paging.publisher

import androidx.paging.PagingSource
import com.google.firebase.firestore.DocumentSnapshot
import com.secret.readit.core.data.publisher.PublisherInfoDataSource
import com.secret.readit.core.paging.BasePagingSource
import com.secret.readit.model.Publisher
import javax.inject.Inject

/**
 * Paging Source which handles loading/caching things related to publishers on homefeed..etc
 */
class PublishersPagingSource @Inject constructor(
    private val pubDataSource: PublisherInfoDataSource
): PagingSource<DocumentSnapshot, Publisher>(), BasePagingSource<RequestParams>{

    override var reqParams: RequestParams = RequestParams(0, 0, emptyList()) //It is consumer responsibility to fill the request

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, Publisher> {
        val result = pubDataSource.getPublishers(reqParams.withIds, reqParams.followersNum, reqParams.limit, params.key)
        return process(result, params)
    }
}