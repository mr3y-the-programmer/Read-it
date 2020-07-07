/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.model

typealias publisherId = String

data class Publisher(val id: publisherId,
                     val name: String,
                     val emailAddress: String,
                     val profileImg: Byte? = null,
                     val thumbnail: Byte? = null,
                     val memberSince: Long,
                     val publishedArticlesIds: List<articleId> = emptyList(),
                     val followedCategoriesIds: List<String> = emptyList(),
                     val followedPublishersIds: List<publisherId> = emptyList(),
                     val numOfFollowers: Int = 0)

//TODO: Update return type of profileImg,
//TODO: move thumbnail to be extension fun/property