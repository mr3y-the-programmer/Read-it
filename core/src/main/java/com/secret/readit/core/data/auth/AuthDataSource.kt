/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.auth

import android.net.Uri

/**
 * Blueprint for information we get from FirebaseAuth
 */
interface AuthDataSource {

    /**
     * User is signed-in if FirebaseUser isn't null
     */
    fun isUserSignedIn(): Boolean

    /**
     * get unique user id, This is Unique within entire firebase project.
     * Don't use it to identify user, instead see [FirebaseUser.getToken()]
     */
    fun getUid(): String?

    /**
     * get the name which will be displayed to other users
     */
    fun getDisplayName(): String?

    /**
     * get Uri of account img
     */
    fun getProfileImgUri(): Uri?

    /**
     * get registered user email address
     */
    fun getEmailAddress(): String?

    /**
     * the first time he signed, joined app
     */
    fun getCreatedSince(): Long?
}
