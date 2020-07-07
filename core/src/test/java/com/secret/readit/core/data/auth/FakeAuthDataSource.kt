/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.auth

import android.net.Uri

class FakeAuthDataSource : AuthDataSource {
    override fun isUserSignedIn(): Boolean {
        return true
    }

    override fun getUid(): String? {
        return TestData.publisher1.id
    }

    override fun getDisplayName(): String? {
        return TestData.publisher1.name
    }

    override fun getProfileImgUri(): Uri? {
        return null
    }

    override fun getEmailAddress(): String? {
        return TestData.publisher1.emailAddress
    }

    override fun getCreatedSince(): Long? {
        return TestData.publisher1.memberSince
    }
}