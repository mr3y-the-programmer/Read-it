/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.domain.search

/**
 * Result search models Exposed to Ui when entering string query
 */
sealed class Searchable {

    data class Article(val title: String, val content: String, val pubName: String, val sinceYear: Int)

    data class Publisher(val name: String, val email: String, val sinceYear: Int, val profileImgUrl: String)
}
