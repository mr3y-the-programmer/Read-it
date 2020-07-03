/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.model

data class Comment(val id: String,
                   val publisher: Publisher,
                   val text: String,
                   val timestamp: Long,
                   val replies: List<Comment>)