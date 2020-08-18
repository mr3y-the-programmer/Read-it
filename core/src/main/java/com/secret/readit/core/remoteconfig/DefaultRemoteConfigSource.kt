/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.remoteconfig

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.secret.readit.BuildConfig
import com.secret.readit.core.di.IoDispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
class DefaultRemoteConfigSource @Inject constructor(private val config: FirebaseRemoteConfig,
                                                    @IoDispatcher private val ioDispatcher: CoroutineDispatcher): RemoteConfigSource {

    private val _contentLimit = MutableStateFlow(config.getLong(RemoteConfigSource.CONTENT_LIMIT_KEY))
    override val contentLimit: StateFlow<Long>
        get() = _contentLimit

    private val _imgPlaceHolder = MutableStateFlow(config.getString(RemoteConfigSource.CONTENT_PLACE_HOLDER_KEY))
    override val imgPlaceHolder: StateFlow<String>
        get() = _imgPlaceHolder

    private val _profileImgPlaceHolder = MutableStateFlow(config.getString(RemoteConfigSource.PROFILE_IMG_KEY))
    override val profileImgPlaceHolder: StateFlow<String>
        get() = _profileImgPlaceHolder

    private val _launchDate = MutableStateFlow(config.getString(RemoteConfigSource.LAUNCH_DATE_KEY))
    override val launchDate: StateFlow<String>
        get() = _launchDate

    private val _pageConfigSizeLimit = MutableStateFlow(config.getLong(RemoteConfigSource.PAGING_CONFIG_LIMIT_KEY))
    override val pageConfigSizeLimit: StateFlow<Long>
        get() = _pageConfigSizeLimit

    private val _searchAppId = MutableStateFlow(config.getString(RemoteConfigSource.SEARCH_APP_ID_KEY))
    override val searchAppId: StateFlow<String>
        get() = _searchAppId

    private val _searchApiKey = MutableStateFlow(config.getString(RemoteConfigSource.SEARCH_API_KEY))
    override val searchApiKey: StateFlow<String>
        get() = _searchApiKey

    private val _isSearchReady = MutableStateFlow(config.getBoolean(RemoteConfigSource.SHIP_SEARCH_KEY))
    override val isSearchReady: StateFlow<Boolean>
        get() = _isSearchReady

    private val _withNumOfFollowers = MutableStateFlow(config.getLong(RemoteConfigSource.WITH_NUM_OF_FOLLOWERS_KEY))
    override val withNumOfFollowers: StateFlow<Long>
        get() = _withNumOfFollowers

    private val _withAppreciateNum = MutableStateFlow(config.getLong(RemoteConfigSource.WITH_APPRECIATE_NUM))
    override val withAppreciateNum: StateFlow<Long>
        get() = _withAppreciateNum

    private val _withMinutesRead = MutableStateFlow(config.getLong(RemoteConfigSource.WITH_MINUTES_READ))
    override val withMinutesRead: StateFlow<Long>
        get() = _withMinutesRead

    private val _shortArtWithAppreciateNum = MutableStateFlow(config.getLong(RemoteConfigSource.SHORT_ARTICLES_WITH_APPRECIATE_NUM))
    override val shortArtWithAppreciateNum: StateFlow<Long>
        get() = _shortArtWithAppreciateNum

    private val _minimumArticlesLimit = MutableStateFlow(config.getLong(RemoteConfigSource.MINIMUM_ARTICLES_LIMIT))
    override val minimumArticlesLimit: StateFlow<Long>
        get() = _minimumArticlesLimit

    private val _maximumArticlesLimit = MutableStateFlow(config.getLong(RemoteConfigSource.MAXIMUM_ARTICLES_LIMIT))
    override val maximumArticlesLimit: StateFlow<Long>
        get() = _maximumArticlesLimit

    private val _categoriesLimit = MutableStateFlow(config.getLong(RemoteConfigSource.CATEGORIES_LIMIT))
    override val categoriesLimit: StateFlow<Long>
        get() = _categoriesLimit

    //TODO: Refactor this ugly boilerplate

    init {
        fetchLatest()
    }

    private fun fetchLatest() {
        CoroutineScope(Job()).launch {
            withContext(ioDispatcher) {
                config
                    .fetch(FETCH_MINIMUM_INTERVAL)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Timber.d("New Remote Config values fetched, Activating....")
                            config
                                .activate()
                                .addOnSuccessListener { updateParams() /* If succeeded update params, else it is already activated before */ }
                        }
                    }
            }
        }
    }

    private fun updateParams() {
        _contentLimit.value = config.getLong(RemoteConfigSource.CONTENT_LIMIT_KEY)
        _imgPlaceHolder.value = config.getString(RemoteConfigSource.CONTENT_PLACE_HOLDER_KEY)
        _profileImgPlaceHolder.value = config.getString(RemoteConfigSource.PROFILE_IMG_KEY)
        _launchDate.value = config.getString(RemoteConfigSource.LAUNCH_DATE_KEY)
        _pageConfigSizeLimit.value = config.getLong(RemoteConfigSource.PAGING_CONFIG_LIMIT_KEY)
        _searchAppId.value = config.getString(RemoteConfigSource.SEARCH_APP_ID_KEY)
        _searchApiKey.value = config.getString(RemoteConfigSource.SEARCH_API_KEY)
        _isSearchReady.value = config.getBoolean(RemoteConfigSource.SHIP_SEARCH_KEY)
        _withNumOfFollowers.value = config.getLong(RemoteConfigSource.WITH_NUM_OF_FOLLOWERS_KEY)
        _withAppreciateNum.value = config.getLong(RemoteConfigSource.WITH_APPRECIATE_NUM)
        _withMinutesRead.value = config.getLong(RemoteConfigSource.WITH_MINUTES_READ)
        _shortArtWithAppreciateNum.value = config.getLong(RemoteConfigSource.SHORT_ARTICLES_WITH_APPRECIATE_NUM)
        _minimumArticlesLimit.value = config.getLong(RemoteConfigSource.MINIMUM_ARTICLES_LIMIT)
        _maximumArticlesLimit.value = config.getLong(RemoteConfigSource.MAXIMUM_ARTICLES_LIMIT)
        _categoriesLimit.value = config.getLong(RemoteConfigSource.CATEGORIES_LIMIT)
    }
    companion object {
        val FETCH_MINIMUM_INTERVAL = if (BuildConfig.DEBUG) 10L else 3600L //We need rapid feedback in Development
    }
}