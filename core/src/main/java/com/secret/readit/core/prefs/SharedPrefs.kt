/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.prefs

import com.secret.readit.model.ThemeType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

/**
 * Blueprint for key-value pairs saved in sharedPreferences file.
 *
 * In designing this Blueprint We decided to represent each value as StateFlow<*> so it is immutable for consumers
 * And Updates should be made through functions/operators not on the value directly.
 *
 * Why we chose StateFlows over Channels(i.e Conflated Channel particularly):
 * -equality conflation
 * -stateFlow can be safely read at any time
 * -stateFlow can be used in two modes: Reactive & non-reactive, In our case we care about non-reactive mode
 * -And more importantly stateFlow is more simpler than channels
 *
 * For more information on stateFlow, see: https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/
 */
@ExperimentalCoroutinesApi
interface SharedPrefs {
    /**
     * hold the user Auth state(logged or not)
     */
    val isUserLoggedIn: StateFlow<Boolean>

    /**
     * hold the current theme value(light, dark...etc)
     */
    val currentTheme: StateFlow<ThemeType>

    /**
     * hold current User/Publisher name
     */
    val currentUserName: StateFlow<String>
}