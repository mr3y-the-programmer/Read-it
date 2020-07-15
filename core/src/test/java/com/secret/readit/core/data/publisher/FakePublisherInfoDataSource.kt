/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.publisher

import com.secret.readit.core.result.Result
import com.secret.readit.model.Publisher
import com.secret.readit.model.articleId
import com.secret.readit.model.publisherId

class FakePublisherInfoDataSource: PublisherInfoDataSource {
    override suspend fun getPublisher(id: publisherId): Result<Publisher> {
        return Result.Success(TestData.publisher1)
    }

    override suspend fun setDisplayName(newName: String, id: publisherId): Result<Boolean> {
        return Result.Success(true)
    }

    override suspend fun addNewArticleId(articleID: articleId, publisherID: publisherId): Result<Boolean> {
        return Result.Success(true)
    }

    override suspend fun removeExistingArticleId(articleID: articleId, publisherID: publisherId): Result<Boolean> {
        return Result.Success(true)
    }

    override suspend fun addNewCategoryId(categoryID: String, publisherID: publisherId): Result<Boolean> {
        return Result.Success(true)
    }

    override suspend fun unFollowExistingCategoryId(categoryID: String, publisherID: publisherId): Result<Boolean> {
        return Result.Success(true)
    }

    override suspend fun follow(
        followedPublisherID: publisherId,
        publisherID: publisherId
    ): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun unFollow(
        unFollowedPublisherID: publisherId,
        publisherID: publisherId
    ): Result<Boolean> {
        TODO("Not yet implemented")
    }
}