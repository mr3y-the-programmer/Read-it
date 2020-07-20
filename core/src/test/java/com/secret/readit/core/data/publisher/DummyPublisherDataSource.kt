/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.publisher

import com.nhaarman.mockitokotlin2.mock
import com.secret.readit.core.result.Result
import com.secret.readit.model.Publisher
import com.secret.readit.model.articleId
import com.secret.readit.model.publisherId

class DummyPublisherDataSource: PublisherInfoDataSource {
    override suspend fun getPublisherId(publisher: PubImportantInfo): Result<publisherId> = mock {  } //no-op

    override suspend fun getPublisher(id: publisherId): Result<Publisher> = mock {  } //no-op

    override suspend fun getPublishersWithFollowers(numOfFollowers: Int, limit: Int): Result<List<Publisher>> = mock {  } //no-op

    override suspend fun setDisplayName(newName: String, id: publisherId): Result<Boolean> = mock {  } //no-op

    override suspend fun addNewArticleId(articleID: articleId, publisherID: publisherId): Result<Boolean> = mock {  } //no-op

    override suspend fun removeExistingArticleId(articleID: articleId, publisherID: publisherId): Result<Boolean> = mock {  } //no-op

    override suspend fun addNewCategoryId(categoryID: String, publisherID: publisherId): Result<Boolean> = mock {  } //no-op

    override suspend fun unFollowExistingCategoryId(categoryID: String, publisherID: publisherId): Result<Boolean> = mock {  } //no-op

    override suspend fun follow(followedPublisherID: publisherId, publisherID: publisherId): Result<Boolean> = mock {  } //no-op

    override suspend fun unFollow(unFollowedPublisherID: publisherId, publisherID: publisherId): Result<Boolean> = mock {  } //no-op
}