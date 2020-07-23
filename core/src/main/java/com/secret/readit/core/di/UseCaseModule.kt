/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.di

import com.secret.readit.core.data.publisher.PublisherRepository
import com.secret.readit.core.domain.UseCase
import com.secret.readit.core.domain.homefeed.MostFollowedPublishersArticles
import com.secret.readit.core.domain.pubprofile.CurrentUserPage
import com.secret.readit.core.prefs.SharedPrefs
import com.secret.readit.core.uimodels.UiPublisher
import com.secret.readit.model.publisherId
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Module
class UseCaseModule {

    @Provides
    @MostFollowedPublishers
    fun provideMostFollowedPublishers(publisherRepo: PublisherRepository): UseCase<Pair<Int, Int>, List<publisherId>> = MostFollowedPublishersArticles(publisherRepo)

    @Provides
    @ExperimentalCoroutinesApi
    @CurrentUserProfile
    fun provideCurrentUserPage(pubRepo: PublisherRepository, prefs: SharedPrefs): UseCase<Unit, UiPublisher> = CurrentUserPage(pubRepo, prefs)
}
