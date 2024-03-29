/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.paging

import androidx.paging.PagingSource
import com.google.firebase.firestore.DocumentSnapshot

/**
 * This is the Base PagingSource that handles childs boilerplate
 */
interface BasePagingSource<params : BaseReqParams> {

    /**
     * Specify params needed to make request of data like limit, ids...etc
     */
    var reqParams: params

    /**
     * Handles providing request data to PagingSource
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> withParams(reqParams: params): PagingSource<DocumentSnapshot, T> {
        val source = apply { this.reqParams = reqParams }
        return (source as PagingSource<DocumentSnapshot, T>)
    }
}
