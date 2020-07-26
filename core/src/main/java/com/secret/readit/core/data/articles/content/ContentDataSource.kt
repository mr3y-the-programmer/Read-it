/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles.content

import com.secret.readit.core.result.Result
import com.secret.readit.model.Element
import com.secret.readit.model.articleId

/**
 * Blueprint for operations on sub-collection content under each article document
 */
interface ContentDataSource {

    /**
     * get content stored under article specified with [articleId]
     */
    suspend fun getContent(id: articleId): Result<List<Element>>

    /**
     * upload Content to firestore specific for this [articleId]
     *
     * @return true on Success, otherwise false
     */
    suspend fun addContent(id: articleId, elements: List<Element>): Result<Boolean>
}