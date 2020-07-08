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
import com.secret.readit.core.data.auth.AuthDataSource
import com.secret.readit.core.data.auth.DefaultAuthDataSource
import dagger.Module
import dagger.Provides
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
    fun provideAuthDataSource(): AuthDataSource {
        return DefaultAuthDataSource(provideFirebaseUser())
    }

    @Provides
    @Singleton
    fun provideFirebaseUser(): FirebaseUser? {
        return provideFirebaseAuth().currentUser
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
    // TODO: In (app) module provideContext() in di module there, it is needed to satisfy AuthRepository
    // TODO: make drafts database
//    TODO: make all dataSources @Singleton
    // TODO: make all dataSources internal
}
