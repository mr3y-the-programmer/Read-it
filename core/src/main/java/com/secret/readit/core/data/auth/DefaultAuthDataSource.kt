/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.auth

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

/**
 * Our AuthDataSource has one responsibility, get Basic Info about signed-in user.
 *
 * **NOTE**: This can be changed soon to get data from provider instead using [firebaseUser.providerData]
 */
class DefaultAuthDataSource @Inject constructor(private val firebaseUser: FirebaseUser?) : AuthDataSource {

    override fun isUserSignedIn(): Boolean = firebaseUser != null

    override fun getUid(): String? {
        return firebaseUser?.uid
    }

    override fun getDisplayName(): String? {
        return firebaseUser?.displayName
    }

    override fun getProfileImgUri(): Uri? {
        return firebaseUser?.photoUrl
    }

    override fun getEmailAddress(): String? {
        return firebaseUser?.email
    }

    override fun getCreatedSince(): Long? {
        return firebaseUser?.metadata?.creationTimestamp
    }
}