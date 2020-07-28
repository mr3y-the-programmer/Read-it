/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles.content

/**
 * This just a temporary(transient) Element model which firestore can store
 */
internal data class TransientFirestoreElement(
    val text: String?,
    val markup: Map<String, String>?,
    val imgUri: String?
)
