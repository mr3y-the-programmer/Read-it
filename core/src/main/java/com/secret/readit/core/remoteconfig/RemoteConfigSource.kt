/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.remoteconfig

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

/**
 * Defines/fetches the data From Remote Config
 */
@ExperimentalCoroutinesApi
interface RemoteConfigSource {

    /**
     * value of [CONTENT_LIMIT_KEY]
     */
    val contentLimit: StateFlow<Long>

    /**
     * value of [CONTENT_PLACE_HOLDER_KEY]
     */
    val imgPlaceHolder: StateFlow<String>

    /**
     * value of [PROFILE_IMG_KEY]
     */
    val profileImgPlaceHolder: StateFlow<String>

    /**
     * value of [LAUNCH_DATE_KEY]
     */
    val launchDate: StateFlow<String>

    /**
     * value of [SEARCH_APP_ID_KEY]
     */
    val searchAppId: StateFlow<String>

    /**
     * value of [SEARCH_API_KEY]
     */
     val searchApiKey: StateFlow<String>

    /**
     * value of [SHIP_SEARCH_KEY]
     */
     val isSearchReady: StateFlow<Boolean>

    /**
     * value of [WITH_NUM_OF_FOLLOWERS_KEY]
     */
     val withNumOfFollowers: StateFlow<Long>

    /**
     * value of [WITH_APPRECIATE_NUM]
     */
     val withAppreciateNum: StateFlow<Long>

    /**
     * value of [WITH_MINUTES_READ]
     */
     val withMinutesRead: StateFlow<Long>

     /**
      * value of [SHORT_ARTICLES_WITH_APPRECIATE_NUM]
      */
      val shortArtWithAppreciateNum: StateFlow<Long>

      /**
       * value of [MINIMUM_ARTICLES_LIMIT]
       */
       val minimumArticlesLimit: StateFlow<Long>

       /**
        * value of [MAXIMUM_ARTICLES_LIMIT]
        */
       val maximumArticlesLimit: StateFlow<Long>

        /**
         * value of [CATEGORIES_LIMIT]
         */
       val categoriesLimit: StateFlow<Long>

    companion object {
        const val CONTENT_LIMIT_KEY = "CONTENT_DISPLAYED_LIMIT"
        const val CONTENT_PLACE_HOLDER_KEY = "IMG_PLACE_HOLDER_URL"
        const val PROFILE_IMG_KEY = "PROFILE_IMG_PLACE_HOLDER_URL"
        const val LAUNCH_DATE_KEY = "LAUNCH_DATE"
        const val SEARCH_APP_ID_KEY = "SEARCH_APP_ID"
        const val SEARCH_API_KEY = "SEARCH_API_KEY"
        const val SHIP_SEARCH_KEY = "SHIP_SEARCH_FEATURE"
        const val WITH_NUM_OF_FOLLOWERS_KEY = "WITH_NUM_OF_FOLLOWERS"
        const val WITH_APPRECIATE_NUM = "WITH_APPRECIATE_NUM"
        const val WITH_MINUTES_READ = "WITH_MINUTES_READ"
        const val SHORT_ARTICLES_WITH_APPRECIATE_NUM = "SHORT_WITH_APPRECIATE_NUM"
        const val MINIMUM_ARTICLES_LIMIT = "MINIMUM_ARTICLES_LIMIT"
        const val MAXIMUM_ARTICLES_LIMIT = "MAXIMUM_ARTICLES_LIMIT"
        const val CATEGORIES_LIMIT = "CATEGORIES_LIMIT"
    }
}