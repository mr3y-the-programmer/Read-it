/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.prefs

import android.app.Application
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.secret.readit.core.uimodels.ThemeType
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/*
    Robolectric Test
 */

@RunWith(AndroidJUnit4::class)
@Config(minSdk = Build.VERSION_CODES.LOLLIPOP, maxSdk = Build.VERSION_CODES.Q)
class SharedPrefsTest {

    // Object under test
    private lateinit var prefs: DefaultSharedPrefs

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        prefs = DefaultSharedPrefs(context)
    }

    @Test
    fun test_Defaults() {
        // Assert shared prefs has default values already set
        assertThat(prefs.isUserLoggedIn.value).isFalse()
        assertThat(prefs.currentTheme.value).isEqualTo("light")
        assertThat(prefs.currentUserName.value).isEmpty()
    }

    @Test
    fun update_ReturnUpdatedValues() {
        // When trying to update sharedPrefs values
        prefs.updateUserAuthState(true)
        prefs.updateCurrentTheme(ThemeType.DARK)
        prefs.updateUserName("Fake1")

        // Assert all Ok
        assertThat(prefs.isUserLoggedIn.value).isTrue()
        assertThat(prefs.currentTheme.value).isEqualTo("dark")
        assertThat(prefs.currentUserName.value).isEqualTo("Fake1")
    }
}
