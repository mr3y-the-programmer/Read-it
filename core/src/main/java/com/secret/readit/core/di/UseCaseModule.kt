/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.di

import com.secret.readit.core.data.publisher.PublisherRepository
import com.secret.readit.core.domain.UseCase
import com.secret.readit.core.domain.articles.MostFollowedPublishersArticles
import com.secret.readit.model.publisherId
import dagger.Module
import dagger.Provides

@Module
class UseCaseModule {

    @Provides
    @MostFollowedPublishers
    fun provideMostFollowedPublishers(publisherRepo: PublisherRepository): UseCase<Pair<Int, Int>, List<publisherId>> = MostFollowedPublishersArticles(publisherRepo)
}