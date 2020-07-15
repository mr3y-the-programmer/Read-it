/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.uimodels

import android.graphics.Bitmap
import com.secret.readit.model.Publisher

/**
 * A Wrapper around publisher main data(stored in firestore) and additional/extension data(i.e bitmap, thumbnail...etc)
 *
 * This is what Ui Consumers expect to have
 */
data class UiPublisher(val publisher: Publisher,
                       val profileImg: Bitmap?)