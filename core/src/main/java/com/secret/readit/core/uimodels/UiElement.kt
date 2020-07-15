/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.uimodels

import android.graphics.Bitmap
import com.secret.readit.model.BaseElement

/**
 * image Element's format that consumers expect to have
 */
data class ImageUiElement(val bitmap: Bitmap?): BaseElement()