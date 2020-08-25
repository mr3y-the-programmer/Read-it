/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.secret.readit.model.ThemeType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Default implementation of SharedPreferences, Any Consumers should interact with this
 */

@Singleton
class DefaultSharedPrefs @Inject constructor(private val applicationContext: Context) : SharedPrefs {

    private val prefs: Lazy<SharedPreferences> = lazy {
        applicationContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
    }

    private val _isUserLoggedIn = MutableStateFlow(USER_LOGGED_IN_DEFAULT_VALUE)
    override val isUserLoggedIn: StateFlow<Boolean>
        get() = _isUserLoggedIn

    private val _currentTheme = MutableStateFlow(CURRENT_THEME_DEFAULT_VALUE)
    override val currentTheme: StateFlow<String>
        get() = _currentTheme

    private val _currentUserName = MutableStateFlow(CURRENT_USER_NAME_DEFAULT_VALUE)
    override val currentUserName: StateFlow<String>
        get() = _currentUserName

    override fun updateUserAuthState(newState: Boolean) = update(USER_LOGGED_IN_KEY, newState, _isUserLoggedIn)

    override fun updateCurrentTheme(newTheme: ThemeType) = update(CURRENT_THEME_KEY, newTheme.label, _currentTheme)

    override fun updateUserName(newName: String) = update(CURRENT_USER_NAME_KEY, newName, _currentUserName)

    private fun <T> update(prefsKey: String, value: T, updatedField: MutableStateFlow<T>) {
        prefs.value.edit {
            if (value is String) putString(prefsKey, value) else putBoolean(prefsKey, value as Boolean)
        }.also {
            updatedField.value = value
        }
    }

    companion object {
        const val PREFS_FILE_NAME = "read_it_preferences"
        const val USER_LOGGED_IN_KEY = "user_logged_in_key"
        const val USER_LOGGED_IN_DEFAULT_VALUE = false
        const val CURRENT_THEME_KEY = "current_theme_key"
        val CURRENT_THEME_DEFAULT_VALUE = ThemeType.LIGHT.label
        const val CURRENT_USER_NAME_KEY = "current_user_name_key"
        const val CURRENT_USER_NAME_DEFAULT_VALUE = ""
    }
}
