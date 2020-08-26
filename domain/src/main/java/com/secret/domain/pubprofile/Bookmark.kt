/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.domain.pubprofile

import com.secret.domain.FlowUseCase
import com.secret.readit.core.data.publisher.PublisherRepository
import com.secret.readit.core.uimodels.UiArticle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Add Article to Bookmarks UseCase
 */
class Bookmark @Inject constructor(private val pubRepo: PublisherRepository) : FlowUseCase<Pair<UiArticle, UpdateBookmark>, Boolean>() {
    override suspend fun execute(parameters: Pair<UiArticle, UpdateBookmark>): Flow<Boolean> {
        return flow {
            when (parameters.second) {
                UpdateBookmark.BOOKMARK -> emit(pubRepo.bookmark(parameters.first))
                UpdateBookmark.UN_BOOKMARK -> emit(pubRepo.unBookmark(parameters.first))
            }
        }
    }
}

/**
 * Define whether to bookmark or unBookmark article
 */
enum class UpdateBookmark {
    BOOKMARK,
    UN_BOOKMARK
}
