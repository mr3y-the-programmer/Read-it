/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.secret.readit.core.data.articles.ArticlesDataSource
import com.secret.readit.core.data.articles.DefaultArticlesDataSource
import com.secret.readit.core.data.articles.NormalizeHelper
import com.secret.readit.core.data.articles.utils.CustomIDHandler
import com.secret.readit.core.data.articles.utils.Parser
import com.secret.readit.core.data.auth.AuthDataSource
import com.secret.readit.core.data.auth.DefaultAuthDataSource
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
class MainModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance()

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
    fun provideParserObject(): Parser {
        return Parser
    }

    @Provides
    fun provideCustomIDHandler(): CustomIDHandler{
        return CustomIDHandler()
    }
    // TODO: make drafts database
//    TODO: make all Repositories @Singleton
    // TODO: make all dataSources internal
    //TODO(optionally): Replace @Provides with @Binds in project's own dependencies
}
