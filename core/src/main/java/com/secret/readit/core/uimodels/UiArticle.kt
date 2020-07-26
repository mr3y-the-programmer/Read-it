/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.uimodels

import com.secret.readit.model.*

/**
 * Article model exposed to domain & presenter layer which hold the expected format for any article
 */
data class UiArticle(val article: Article,
                     val publisher: UiPublisher,
                     val category: List<Category>)
