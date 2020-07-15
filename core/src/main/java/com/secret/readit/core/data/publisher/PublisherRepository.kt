/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.publisher

import android.graphics.Bitmap
import android.net.Uri
import com.secret.readit.core.data.auth.AuthRepository
import com.secret.readit.core.data.shared.StorageRepository
import com.secret.readit.core.result.Result
import com.secret.readit.core.result.succeeded
import com.secret.readit.core.uimodels.UiPublisher
import com.secret.readit.model.Publisher
import javax.inject.Inject

/**
 * Any consumers should interact with this Repo not with DataSources directly,
 * Rule: -forward actions when needed to dataSource
 *       -provide Publisher data to consumers in expected format
 */
class PublisherRepository @Inject constructor(private val publisherDataSource: PublisherInfoDataSource,
                                              private val authRepo: AuthRepository,
                                              private val storageRepo: StorageRepository) {

    /**
     * get Current Signed-in publisher/user, empty Publisher if no signed-in user
     */
    suspend fun getCurrentUser(): UiPublisher{
        val id = authRepo.getId() ?: return getEmptyPublisher()

        val result = publisherDataSource.getPublisher(id)

        var publisher = getEmptyPublisher().publisher
        var profileImg: Bitmap? = null
        if (result.succeeded){
            publisher = (result as Result.Success).data
            val imgUri = Uri.parse(publisher.profileImgUri)
            profileImg = storageRepo.downloadImg(imgUri, DEFAULT_PROFILE_IMG_URL)
        }
        return UiPublisher(publisher = publisher, profileImg = profileImg)
    }

    /**
     * update Current signed-in user name,
     * @return true on success, false on failure or no signed-in user
     */
    suspend fun updateName(newName: String): Boolean {
        val id = authRepo.getId() ?: return false //User isn't Signed-in
        val result = publisherDataSource.setDisplayName(newName, id)
        return if (result.succeeded) (result as Result.Success).data else false
    }

    private fun getEmptyPublisher(): UiPublisher {
        val pub = Publisher(id = "", name = "", emailAddress = "", memberSince = -1)
        return UiPublisher(publisher = pub, profileImg = null)
    }

    companion object {
        const val DEFAULT_PROFILE_IMG_URL = "https://firebasestorage.googleapis.com/v0/b/read-it-b9c8b.appspot.com/" +
                "o/publishers%2Faccount_default.png?alt=media&token=1502d0e1-c30f-4d45-997d-e39ac5af62ba"
    }
}