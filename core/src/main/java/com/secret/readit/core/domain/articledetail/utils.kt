/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.domain.articledetail

import com.secret.readit.core.data.articles.ArticlesRepository
import com.secret.readit.core.uimodels.UiComment

fun List<UiComment>.sort(): List<UiComment>{
    return sortedWith(compareByDescending<UiComment> { it.comment.repliesIds.size }
        .thenByDescending { it.pub.publisher.numOfFollowers }
        .thenByDescending { it.comment.timestamp })
}

fun currentArtID(instance: ArticlesRepository) = instance.currentArticleID ?: throw NullPointerException()