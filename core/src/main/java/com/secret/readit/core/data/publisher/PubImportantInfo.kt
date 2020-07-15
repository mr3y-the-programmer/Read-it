/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.publisher

/**
 * data class which contain the needed Info for publisher so we can get its id from firestore
 */
data class PubImportantInfo(val name: String,
                            val emailAddress: String,
                            val memberSince: Long)