/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.domain.homefeed

import com.secret.readit.core.domain.UseCase
import com.secret.readit.model.publisherId

class FakeMostFollowedPublishers : UseCase<Pair<Int, Int>, List<publisherId>>() {
    override suspend fun execute(parameters: Pair<Int, Int>): List<publisherId> = TestData.mostFollowedIds
}
