/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.secret.readit.R
import com.secret.readit.core.data.articles.ArticlesDataSource
import com.secret.readit.core.data.articles.ArticlesRepository
import com.secret.readit.core.data.articles.DefaultArticlesDataSource
import com.secret.readit.core.data.articles.NormalizeHelper
import com.secret.readit.core.data.articles.comments.CommentDataSource
import com.secret.readit.core.data.articles.comments.DefaultCommentsDataSource
import com.secret.readit.core.data.articles.content.ContentDataSource
import com.secret.readit.core.data.articles.content.DefaultContentDataSource
import com.secret.readit.core.data.articles.utils.Formatter
import com.secret.readit.core.data.auth.AuthDataSource
import com.secret.readit.core.data.auth.AuthRepository
import com.secret.readit.core.data.auth.DefaultAuthDataSource
import com.secret.readit.core.data.categories.CategoryDataSource
import com.secret.readit.core.data.categories.CategoryRepository
import com.secret.readit.core.data.categories.DefaultCategoryDataSource
import com.secret.readit.core.data.publisher.DefaultPublisherInfoDataSource
import com.secret.readit.core.data.publisher.PublisherInfoDataSource
import com.secret.readit.core.data.publisher.PublisherRepository
import com.secret.readit.core.data.shared.Converter
import com.secret.readit.core.data.shared.DefaultStorageDataSource
import com.secret.readit.core.data.shared.StorageDataSource
import com.secret.readit.core.data.shared.StorageRepository
import com.secret.readit.core.paging.BasePagingSource
import com.secret.readit.core.paging.articles.ArticlesPagingSource
import com.secret.readit.core.paging.articles.PubArticlesPagingSource
import com.secret.readit.core.paging.articles.RequestParams
import com.secret.readit.core.paging.categories.CategoriesPagingSource
import com.secret.readit.core.paging.publisher.PubProfilePagingSource
import com.secret.readit.core.paging.publisher.PublishersPagingSource
import com.secret.readit.core.prefs.DefaultSharedPrefs
import com.secret.readit.core.prefs.SharedPrefs
import com.secret.readit.core.remoteconfig.DefaultRemoteConfigSource
import com.secret.readit.core.remoteconfig.RemoteConfigSource
import dagger.Lazy
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton
import com.secret.readit.core.paging.categories.RequestParams as categoryParams
import com.secret.readit.core.paging.publisher.RequestParams as pubParams

@Module
class MainModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        val firestore = Firebase.firestore

        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        return firestore
    }

    @Provides
    fun provideAuthDataSource(user: FirebaseUser): AuthDataSource {
        return DefaultAuthDataSource(user)
    }

    @Provides
    @Singleton
    fun provideFirebaseUser(auth: FirebaseAuth): FirebaseUser? {
        return auth.currentUser
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    fun provideArticlesDataSource(
        firestore: FirebaseFirestore,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): ArticlesDataSource {
        return DefaultArticlesDataSource(firestore, ioDispatcher, NormalizeHelper())
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return Firebase.storage
    }

    @Provides
    @Singleton
    fun provideRemoteConfig(): FirebaseRemoteConfig {
        val remoteConfig = Firebase.remoteConfig
        val settings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600 // During Development, We need to low this value. Override this by using another value in fetch(long)
            fetchTimeoutInSeconds = 60 // Also set the timeout to 1 minute
        }
        remoteConfig.setConfigSettingsAsync(settings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        return remoteConfig
    }

    @Provides
    fun provideRemoteConfigDataSource(config: FirebaseRemoteConfig, @IoDispatcher ioDispatcher: CoroutineDispatcher): RemoteConfigSource {
        return DefaultRemoteConfigSource(config, ioDispatcher)
    }

    @Provides
    fun provideInStreamConverter(@DefaultDispatcher defaultDispatcher: CoroutineDispatcher): Converter {
        return Converter(defaultDispatcher)
    }

    @Provides
    fun provideStorageDataSource(
        storage: FirebaseStorage,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        converter: Lazy<Converter>,
        prefs: SharedPrefs
    ): StorageDataSource {
        return DefaultStorageDataSource(storage, ioDispatcher, converter, prefs)
    }

    @Provides
    fun provideCategoryDataSource(firestore: FirebaseFirestore, @IoDispatcher ioDispatcher: CoroutineDispatcher): CategoryDataSource {
        return DefaultCategoryDataSource(firestore, ioDispatcher)
    }

    @Provides
    fun providePublisherInfoDataSource(firestore: FirebaseFirestore, @IoDispatcher ioDispatcher: CoroutineDispatcher): PublisherInfoDataSource {
        return DefaultPublisherInfoDataSource(firestore, ioDispatcher)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(authDataSource: AuthDataSource, firestore: FirebaseFirestore, @IoDispatcher ioDispatcher: CoroutineDispatcher): AuthRepository {
        return AuthRepository(authDataSource, firestore, ioDispatcher)
    }

    @Provides
    @Singleton
    fun provideStorageRepository(storageSource: StorageDataSource): StorageRepository {
        return StorageRepository(storageSource)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(applicationContext: Context): SharedPrefs {
        return DefaultSharedPrefs(applicationContext)
    }

    @Provides
    @Singleton
    fun providePublisherRepository(
        publisherDataSource: PublisherInfoDataSource,
        @PublishersSource pubsPagingSource: BasePagingSource<pubParams>,
        @PubProfileSource pubProfilePagingSource: BasePagingSource<pubParams>,
        authRepo: AuthRepository,
        storageRepo: StorageRepository
    ): PublisherRepository {
        return PublisherRepository(publisherDataSource, pubsPagingSource, pubProfilePagingSource, authRepo, storageRepo)
    }

    @Provides
    fun provideArticlesFormatter(storageRepo: StorageRepository, pubRepo: PublisherRepository, categoryRepo: CategoryRepository): Formatter {
        return Formatter(storageRepo, pubRepo, categoryRepo)
    }

    @Provides
    @Singleton
    fun provideArticlesRepository(
        articlesSource: ArticlesDataSource,
        contentSource: ContentDataSource,
        commentsSource: CommentDataSource,
        @HomeFeedSource articlesPagingSource: BasePagingSource<RequestParams>,
        @PubArticlesSource pubArticlesPagingSource: BasePagingSource<RequestParams>,
        formatter: Formatter,
        configSource: RemoteConfigSource
    ): ArticlesRepository {
        return ArticlesRepository(articlesSource, contentSource, commentsSource, articlesPagingSource, pubArticlesPagingSource, formatter, configSource)
    }

    @Provides
    @Singleton
    fun provideCategoriesRepository(categoryDataSource: CategoryDataSource, categoryPagingSource: BasePagingSource<categoryParams>): CategoryRepository {
        return CategoryRepository(categoryDataSource, categoryPagingSource)
    }

    @Provides
    fun provideContentDataSource(firestore: FirebaseFirestore, @IoDispatcher dispatcher: CoroutineDispatcher): ContentDataSource {
        return DefaultContentDataSource(firestore, dispatcher)
    }

    @Provides
    fun provideCommentsDataSource(firestore: FirebaseFirestore, @IoDispatcher dispatcher: CoroutineDispatcher): CommentDataSource {
        return DefaultCommentsDataSource(firestore, dispatcher)
    }

    @Provides
    @HomeFeedSource
    fun provideArticlesPagingSource(articlesSource: ArticlesDataSource, contentSource: ContentDataSource): BasePagingSource<RequestParams> {
        return ArticlesPagingSource(articlesSource, contentSource)
    }

    @Provides
    @PubArticlesSource
    fun providePubPagingSource(articlesSource: ArticlesDataSource, contentSource: ContentDataSource): BasePagingSource<RequestParams> {
        return PubArticlesPagingSource(articlesSource, contentSource)
    }

    @Provides
    @PublishersSource
    fun providePublishersPagingSource(pubDataSource: PublisherInfoDataSource): BasePagingSource<pubParams> {
        return PublishersPagingSource(pubDataSource)
    }

    @Provides
    @PubProfileSource
    fun providePubProfilePagingSource(pubDataSource: PublisherInfoDataSource): BasePagingSource<pubParams> {
        return PubProfilePagingSource(pubDataSource)
    }

    @Provides
    fun provideCategoriesPagingSource(categoryDataSource: CategoryDataSource): BasePagingSource<categoryParams> {
        return CategoriesPagingSource(categoryDataSource)
    }
    // TODO: make drafts database
}
