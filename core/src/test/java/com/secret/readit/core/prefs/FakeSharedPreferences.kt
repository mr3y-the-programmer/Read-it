/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.prefs

import com.secret.readit.core.uimodels.ThemeType
import kotlinx.coroutines.flow.StateFlow

class FakeSharedPreferences : SharedPrefs {

    override val isUserLoggedIn: StateFlow<Boolean>
        get() = TODO("Not yet implemented")

    override val currentTheme: StateFlow<String>
        get() = TODO("Not yet implemented")

    override val currentUserName: StateFlow<String>
        get() = TODO("Not yet implemented")

    override val currentUploadSessionUri: StateFlow<String>
        get() = TODO("Not yet implemented")

    override fun updateUserAuthState(newState: Boolean) {
        TODO("Not yet implemented")
    }

    override fun updateCurrentTheme(newTheme: ThemeType) {
        TODO("Not yet implemented")
    }

    override fun updateUserName(newName: String) {
        TODO("Not yet implemented")
    }

    override fun updateUploadUri(newUri: String) {
        TODO("Not yet implemented")
    }
}
