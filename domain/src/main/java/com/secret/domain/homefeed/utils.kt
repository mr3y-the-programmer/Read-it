/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.domain.homefeed

import androidx.paging.PagingData
import androidx.paging.filter
import com.secret.readit.core.uimodels.UiArticle

fun dropEmptyArticles(page: PagingData<UiArticle>) = page.filter { it.article.id.isNotEmpty() && it.article.timestamp > 0 }
