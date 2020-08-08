/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.domain.pubprofile

import com.secret.readit.core.TestData
import com.secret.readit.core.domain.UseCase
import com.secret.readit.core.uimodels.UiPublisher

class FakeCurrentUser : UseCase<Unit, UiPublisher>() {
    override suspend fun execute(parameters: Unit): UiPublisher {
        return TestData.uiPublisher1
    }
}
