/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.search.main

/**
 * Result search models Exposed to Ui when entering string query
 */
sealed class Searchable {

    data class SearchableArticle(val title: String, val content: String, val pubName: String, val since: Long) : Searchable()

    data class SearchablePublisher(val name: String, val email: String, val since: Long, val profileImgUrl: String) : Searchable()
}
