/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.utils

import com.secret.readit.model.Article
import com.secret.readit.model.Category
import com.secret.readit.model.Comment
import timber.log.Timber
import kotlin.IllegalArgumentException

/**
 * CustomIDHandler has one responsibility to get(generate) ids of article, category, comment....etc
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
        val pubId = article.publisherID
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

    /**
     * get an Id for each category
     *
     * @throws IllegalArgumentException if (name) of category wasn't valid
     * @return the id generated
     */
    fun getID(category: Category): String {
        val name = category.name
        if (name.length < 2) {
            Timber.e("not valid category, check its name")
            throw IllegalArgumentException("Cannot create an id for category: $category")
        }

        val firstChar = name[0]
        val lastChar = name[name.length.minus(1)]
        val randomPrefix = when (firstChar.toUpperCase()) {
            in 'A'..'D' -> "a${firstChar}d"
            in 'E'..'O' -> "e${firstChar}o"
            in 'P'..'T' -> "p${firstChar}t"
            else -> "u${firstChar}z"
        }

        val randomSuffix = when (lastChar.toUpperCase()) {
            in 'A'..'D' -> "a${lastChar}d"
            in 'E'..'O' -> "e${lastChar}o"
            in 'P'..'T' -> "p${lastChar}t"
            else -> "u${lastChar}z"
        }

        return "$randomPrefix$DASH$name$DASH$randomSuffix"
    }

    /**
     * get an Id for each comment
     *
     * @throws IllegalArgumentException if (publisherId, timestamp) of comment wasn't valid
     * @return the id generated
     */
    fun getID(comment: Comment): String {
        val pubID = comment.publisherId
        val timestamp = comment.timestamp.toString()

        if (timestamp.length < 7 || pubID.length < 4) {
            Timber.d("Cannot make id for comment: $comment")
            throw IllegalArgumentException("Cannot make an id for Invalid comment")
        }
        val first = pubID.substring(0, 4)
        val last = timestamp.substring(5, timestamp.length)
        return "$first$DASH$last"
    }

    companion object {
        const val DASH = "-"
    }
}
