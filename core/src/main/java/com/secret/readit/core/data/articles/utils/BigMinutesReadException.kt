/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles.utils

/**
 * Exception to be thrown if article has very big minutes read number
 */
class BigMinutesReadException : Exception() {
    override val message: String?
        get() = EXCEPTION_MESSAGE

    companion object {
        const val EXCEPTION_MESSAGE = "Article is too big to be read by anyone try to summarize some of your words or split your article into series"
    }
}
