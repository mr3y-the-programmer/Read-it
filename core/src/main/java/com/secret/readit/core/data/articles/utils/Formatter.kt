/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles.utils

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.secret.readit.core.data.articles.ArticlesRepository
import com.secret.readit.core.data.categories.CategoryRepository
import com.secret.readit.core.data.publisher.PublisherRepository
import com.secret.readit.core.data.shared.StorageRepository
import com.secret.readit.core.data.utils.isImageElement
import com.secret.readit.core.data.utils.isTextElement
import com.secret.readit.core.result.Result
import com.secret.readit.core.result.succeeded
import com.secret.readit.core.uimodels.ImageUiElement
import com.secret.readit.core.uimodels.UiArticle
import com.secret.readit.model.*
import javax.inject.Inject

/**
 * Handles Formatting Articles to expected Format and vice-versa, Used mainly by ArticlesRepo
 */
class Formatter @Inject constructor(
    private val storageRepo: StorageRepository,
    private val pubRepo: PublisherRepository,
    private val categoryRepo: CategoryRepository,
    private val parser: Parser = Parser
) {

    /**
     * format articles in expected format for consumers
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun <T> formatArticles(result: Result<T>, singleItem: Boolean = false): MutableList<UiArticle> {
        val formattedArticles = mutableListOf<UiArticle>()
        if (result != null && result.succeeded) {
            val data = (result as Result.Success).data
            val dataList = mutableListOf<Article>()
            if (singleItem) {
                dataList.add(data as Article)
            } else {
                dataList.addAll(data as List<Article>)
            }
            for (article in dataList) {
                val publisher = pubRepo.getPublisherInfo(article.publisherID)
                val categories = categoryRepo.getCategories(article.categoryIds)
                formattedArticles += UiArticle(article, publisher = publisher, category = categories)
            }
        }
        return formattedArticles
    }

    /**
     * format specific Pub articles in expected format for consumers
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun formatPubArticles(result: Result<Pair<List<Article>, DocumentSnapshot>>): MutableList<UiArticle> {
        val formattedArticles = mutableListOf<UiArticle>()
        if (result != null && result.succeeded) {
            val data = (result as Result.Success).data.first
            for (article in data) {
                val publisher = pubRepo.getPublisherInfo(article.publisherID)
                val categories = categoryRepo.getCategories(article.categoryIds)
                formattedArticles += UiArticle(article, publisher = publisher, category = categories)
            }
        }
        return formattedArticles
    }

    // Handle formatting elements
    private suspend fun formatElements(elements: List<Element>): MutableList<BaseElement> {
        val formattedElements = mutableListOf<BaseElement>()
        for (baseElement in elements) {
            var firestoreElement = baseElement
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

    suspend fun deFormatElements(id: articleId, elements: List<BaseElement>): List<Element> {
        val firestoreElements = mutableListOf<Element>()
        for (element in elements) {
            if (element.isTextElement) { // reverse only text
                var textElement = element as Element
                val deFormattedString = parser.reverseParse(textElement)
                textElement = textElement.copy(text = deFormattedString)
                firestoreElements += textElement
            }
            if (element.isImageElement) {
                val imageElement = element as ImageUiElement
                val downloadUri = storageRepo.uploadImg(id, imageElement.imgPath) ?: Uri.parse(ArticlesRepository.PLACE_HOLDER_URL)
                firestoreElements += Element(downloadUri.toString())
            }
        }
        return firestoreElements
    }
}
