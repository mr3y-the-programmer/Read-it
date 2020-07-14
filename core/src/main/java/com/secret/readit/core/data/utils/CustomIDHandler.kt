/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.utils

import com.secret.readit.model.Article
import timber.log.Timber
import java.lang.IllegalArgumentException

/**
 * CustomIDHandler has one responsibility to get ids of each article
 */
class CustomIDHandler {

    /**
     * get an Id for each article
     *
     * @throws IllegalArgumentException if (timestamp, publisherId, title) of article wasn't valid
     * @return the id generated
     */
    fun getID(article: Article): String {
        val stringTimestamp = article.timestamp.toString()
        val pubId = article.publisher.id
        val title = article.title

        if (title.length < 4 || stringTimestamp.length < 7 || pubId.length < 4) {
            Timber.d("Cannot make id for article: $article")
            throw IllegalArgumentException("Cannot make an id for Invalid article")
        }
        val first = stringTimestamp.substring(5, stringTimestamp.length)
        val middle = pubId.substring(0, 4)
        val last = title.substring(0, 4)

        return "$first$DASH$middle$DASH$last"
    }

    companion object {
        const val DASH = "-"
    }
}
