/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.uimodels

import com.secret.readit.model.Article
import com.secret.readit.model.Category
import com.secret.readit.model.Content

/**
 * Article model exposed to domain & presenter layer which hold the expected format for any article
 */
data class UiArticle(
    val article: Article,
    val publisher: UiPublisher,
    val initialContent: Content, // Content that displayed as a Summery like: article content on home feed page
    val fullContent: Content = initialContent, // Full Content displayed when clicking on article, displaying the full article
    val category: List<Category>,
    val comments: List<UiComment> = emptyList()
)
