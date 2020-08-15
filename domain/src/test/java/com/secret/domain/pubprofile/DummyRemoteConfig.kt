/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.domain.pubprofile

import com.secret.readit.core.remoteconfig.RemoteConfigSource
import kotlinx.coroutines.flow.StateFlow

/**
 * Just a dummy Remote Config to satisfy dependencies, maybe removed later
 */
class DummyRemoteConfig: RemoteConfigSource {
    override val contentLimit: StateFlow<Long>
        get() = TODO("Not yet implemented")
    override val imgPlaceHolder: StateFlow<String>
        get() = TODO("Not yet implemented")
    override val profileImgPlaceHolder: StateFlow<String>
        get() = TODO("Not yet implemented")
    override val launchDate: StateFlow<String>
        get() = TODO("Not yet implemented")
    override val searchAppId: StateFlow<String>
        get() = TODO("Not yet implemented")
    override val searchApiKey: StateFlow<String>
        get() = TODO("Not yet implemented")
    override val isSearchReady: StateFlow<Boolean>
        get() = TODO("Not yet implemented")
    override val withNumOfFollowers: StateFlow<Long>
        get() = TODO("Not yet implemented")
    override val withAppreciateNum: StateFlow<Long>
        get() = TODO("Not yet implemented")
    override val withMinutesRead: StateFlow<Long>
        get() = TODO("Not yet implemented")
    override val shortArtWithAppreciateNum: StateFlow<Long>
        get() = TODO("Not yet implemented")
    override val minimumArticlesLimit: StateFlow<Long>
        get() = TODO("Not yet implemented")
    override val maximumArticlesLimit: StateFlow<Long>
        get() = TODO("Not yet implemented")
    override val categoriesLimit: StateFlow<Long>
        get() = TODO("Not yet implemented")
}