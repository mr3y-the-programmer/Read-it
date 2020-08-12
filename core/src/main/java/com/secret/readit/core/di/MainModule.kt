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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
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
import com.secret.readit.core.paging.ArticlesPagingSource
import com.secret.readit.core.paging.BasePagingSource
import com.secret.readit.core.paging.PubArticlesPagingSource
import com.secret.readit.core.prefs.DefaultSharedPrefs
import com.secret.readit.core.prefs.SharedPrefs
import dagger.Lazy
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

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
    fun provideInStreamConverter(@DefaultDispatcher defaultDispatcher: CoroutineDispatcher): Converter {
        return Converter(defaultDispatcher)
    }

    @Provides
    fun provideStorageDataSource(
        storage: FirebaseStorage,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        converter: Lazy<Converter>
    ): StorageDataSource {
        return DefaultStorageDataSource(storage, ioDispatcher, converter)
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
    @ExperimentalCoroutinesApi
    @Singleton
    fun provideSharedPreferences(applicationContext: Context): SharedPrefs {
        return DefaultSharedPrefs(applicationContext)
    }

    @Provides
    @Singleton
    fun providePublisherRepository(
        publisherDataSource: PublisherInfoDataSource,
        authRepo: AuthRepository,
        storageRepo: StorageRepository
    ): PublisherRepository {
        return PublisherRepository(publisherDataSource, authRepo, storageRepo)
    }

    @Provides
    fun provideArticlesFormatter(contentSource: ContentDataSource, storageRepo: StorageRepository, pubRepo: PublisherRepository, categoryRepo: CategoryRepository): Formatter {
        return Formatter(contentSource, storageRepo, pubRepo, categoryRepo)
    }

    /*@Provides
    @Singleton
    fun provideArticlesRepository(articlesSource: ArticlesDataSource, commentsSource: CommentDataSource, formatter: Formatter): ArticlesRepository {
        return ArticlesRepository(articlesSource, commentsSource, formatter)
    }*/

    @Provides
    @Singleton
    fun provideCategoriesRepository(categoryDataSource: CategoryDataSource): CategoryRepository {
        return CategoryRepository(categoryDataSource)
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
    fun provideArticlesPagingSource(articlesSource: ArticlesDataSource, contentSource: ContentDataSource): BasePagingSource {
        return ArticlesPagingSource.create(articlesSource, contentSource)
    }

    @Provides
    @PubArticlesSource
    fun providePubPagingSource(articlesSource: ArticlesDataSource, contentSource: ContentDataSource): BasePagingSource {
        return PubArticlesPagingSource(articlesSource, contentSource)
    }
    // TODO: make drafts database
}
