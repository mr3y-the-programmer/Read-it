/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.result

sealed class Result<out R> {

    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

/**
 * This extension property prevent throwing ClassCastException when Result cannot cast to Success
 *
 * Test which revealed the bug: [ArticlesRepositoryTest#dataSourceFails_ReturnEmptyArticles()]
 */
//TODO: update AuthRepository to use it
val <T> Result<T>.succeeded
     get() = (this is Result.Success<T>) && this.data != null
