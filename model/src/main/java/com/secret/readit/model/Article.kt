/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.model

typealias articleId = String

data class Article(val id: articleId,
                   val title: String,
                   val content: Content,
                   val publisher: Publisher,
                   val numMinutesRead: Int,
                   val timestamp: Long,
                   val comments: List<Comment>,
                   val numOfAppreciate: Int = 0,
                   val numOfProtest: Int = 0,
                   val isBookmarked: Boolean = false,
                   val category: Category)