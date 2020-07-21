/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles.utils

import android.net.Uri
import com.secret.readit.core.data.articles.ArticlesRepository
import com.secret.readit.core.data.shared.StorageRepository
import com.secret.readit.core.result.Result
import com.secret.readit.core.result.succeeded
import com.secret.readit.core.uimodels.ImageUiElement
import com.secret.readit.model.Article
import com.secret.readit.model.BaseElement
import com.secret.readit.model.Content
import com.secret.readit.model.Element
import javax.inject.Inject

/**
 * Handles Formatting Articles to expected Format
 */
class Formatter @Inject constructor(
    private val storageRepo: StorageRepository,
    private val parser: Parser = Parser
) {

    /**
     * format articles in expected format for consumers
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun <T> formatArticles(result: Result<T>, singleItem: Boolean = false): MutableList<Article> {
        val formattedArticles = mutableListOf<Article>()
        if (result != null && result.succeeded) {
            val data = (result as Result.Success).data
            val dataList = mutableListOf<Article>()
            if (singleItem) {
                dataList.add(data as Article)
            } else {
                dataList.addAll(data as List<Article>)
            }
            for (article in dataList) {
                var parsedArticle = article
                val formattedElements = formatContent(article.content)
                parsedArticle = parsedArticle.copy(content = Content(formattedElements))
                formattedArticles += parsedArticle
            }
        }
        return formattedArticles
    }

    //Handle formatting content
    private suspend fun formatContent(content: Content): MutableList<BaseElement> {
        val formattedElements = mutableListOf<BaseElement>()
        for (baseElement in content.elements) {
            var firestoreElement = (baseElement as Element)
            if (firestoreElement.imageUri == null) { // parse text only
                firestoreElement = parser.parse(baseElement.text!!)
                formattedElements += firestoreElement // In this case UiElement is the same as firestoreElement
            }
            if (firestoreElement.imageUri != null) {
                val imgUri = Uri.parse(firestoreElement.imageUri)
                val bitmap = storageRepo.downloadImg(imgUri, ArticlesRepository.PLACE_HOLDER_URL)
                formattedElements += ImageUiElement(bitmap, firestoreElement.imageUri!!)
            }
        }
        return formattedElements
    }
}