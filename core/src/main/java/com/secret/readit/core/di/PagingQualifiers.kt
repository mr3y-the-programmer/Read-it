/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HomeFeedSource

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PubArticlesSource

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PublishersSource

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PubProfileSource
