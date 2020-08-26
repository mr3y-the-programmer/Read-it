/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.domain.pubprofile

import com.secret.domain.UseCase
import com.secret.readit.core.data.publisher.PubImportantInfo
import com.secret.readit.core.data.publisher.PublisherRepository
import com.secret.readit.core.uimodels.UiPublisher
import javax.inject.Inject

/**
 * GetPubMainInfo Usecase which used when entering any pub profile page,
 * it loads only the basic information like: name, profileImg, rank...etc
 */
class GetPubMainInfo @Inject constructor(private val pubRepo: PublisherRepository) : UseCase<PubImportantInfo, UiPublisher?>() {

    override suspend fun execute(parameters: PubImportantInfo): UiPublisher? {
        val pubId = pubRepo.getPublisherId(parameters) ?: return null
        return pubRepo.getPublisherInfo(pubId)
    }
}
