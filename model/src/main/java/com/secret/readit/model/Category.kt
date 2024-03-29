/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.model

data class Category(val id: String,
                    val name: String,
                    val color: Int,
                    val articleIds: List<articleId> = emptyList())