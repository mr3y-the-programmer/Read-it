/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.uimodels

import com.secret.readit.model.Comment

/**
 * the comment model exposed to domain & presenter layer which contain the expected format for comment
 */
data class UiComment(val comment: Comment,
                     val pub: UiPublisher,
                     val replies: List<UiComment> = emptyList())