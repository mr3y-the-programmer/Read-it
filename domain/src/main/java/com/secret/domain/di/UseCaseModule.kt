/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.domain.di

import com.secret.domain.UseCase
import com.secret.domain.homefeed.MostFollowedPublishersArticles
import com.secret.domain.pubprofile.CurrentUserPage
import com.secret.readit.core.data.publisher.PublisherRepository
import com.secret.readit.core.prefs.SharedPrefs
import com.secret.readit.core.uimodels.UiPublisher
import com.secret.readit.model.publisherId
import dagger.Module
import dagger.Provides

@Module
class UseCaseModule {

    @Provides
    @MostFollowedPublishers
    fun provideMostFollowedPublishers(publisherRepo: PublisherRepository): UseCase<Pair<Int, Int>, List<publisherId>> = MostFollowedPublishersArticles(publisherRepo)

    @Provides
    @CurrentUserProfile
    fun provideCurrentUserPage(pubRepo: PublisherRepository, prefs: SharedPrefs): UseCase<Unit, UiPublisher> = CurrentUserPage(pubRepo, prefs)
}
