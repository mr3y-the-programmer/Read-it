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
import com.secret.readit.core.data.articles.content.ContentDataSource
import com.secret.readit.core.data.categories.CategoryRepository
import com.secret.readit.core.data.publisher.PublisherRepository
import com.secret.readit.core.data.shared.StorageRepository
import com.secret.readit.core.data.utils.CustomIDHandler
import com.secret.readit.core.data.utils.isImageElement
import com.secret.readit.core.data.utils.isTextElement
import com.secret.readit.core.result.Result
import com.secret.readit.core.result.succeeded
import com.secret.readit.core.uimodels.ImageUiElement
import com.secret.readit.core.uimodels.UiArticle
import com.secret.readit.model.*
import timber.log.Timber
import java.lang.IllegalArgumentException
import java.time.Instant
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.math.roundToLong

/**
 * Handles Formatting Articles to expected Format and vice-versa, Used mainly by ArticlesRepo
 */
class Formatter @Inject constructor(
    private val contentDataSource: ContentDataSource,
    private val storageRepo: StorageRepository,
    private val pubRepo: PublisherRepository,
    private val categoryRepo: CategoryRepository,
    private val parser: Parser = Parser,
    private val idHandler: CustomIDHandler = CustomIDHandler()
) {

    /** format articles in expected format for consumers, parameter [contentLimit] to limit the content loaded from dataSource */
    suspend fun formatArticles(result: Result<List<Article>>, contentLimit: Int) = format(result, contentLimit)

    /** format partial/Summery article into full-content article */
    suspend fun formatFullArticle(summeryArticle: UiArticle): UiArticle {
        val fullContent = getExpectedElements(summeryArticle.article.id, 0)
        return summeryArticle.copy(fullContent = Content(fullContent))
    }

    /** format specific Pub articles in expected format for consumers */
    suspend fun formatPubArticles(result: Result<Pair<List<Article>, DocumentSnapshot>>, contentLimit: Int) = format(result, contentLimit, true)

    //Refactor boilerplate to this private fun
    @Suppress("UNCHECKED_CAST")
    private suspend fun <T> format(result: Result<T>, contentLimit: Int, isResultOfPair: Boolean = false): List<UiArticle> {
        val formattedArticles = mutableListOf<UiArticle>()
        if (result != null && result.succeeded) {
            (result as Result.Success).data.let {
                val articles = if (isResultOfPair) (it as Pair<List<Article>, *>).first else (it as List<Article>)
                for (article in articles) {
                    val publisher = pubRepo.getPublisherInfo(article.publisherID)
                    val initialContent = getExpectedElements(article.id, contentLimit)
                    val categories = categoryRepo.getCategories(article.categoryIds)
                    formattedArticles += UiArticle(article, publisher = publisher, category = categories, initialContent = Content(initialContent))
                }
            }
        }
        return formattedArticles
    }

    // Handle formatting elements
    private suspend fun formatElements(elements: List<Element>): List<BaseElement> {
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

    private suspend fun getExpectedElements(id: articleId, limit: Int): List<BaseElement> {
        val result = contentDataSource.getContent(id, limit)
        if (result != null && result.succeeded) {
            val contentElements = (result as Result.Success).data
            return formatElements(contentElements)
        }
        return emptyList()
    }

    //deFormatting section
    suspend fun deFormatArticle(uiArticle: UiArticle): Pair<Article, List<Element>>? {
        val timestamp = Instant.now().toEpochMilli()
        val id = try {
            idHandler.getID(uiArticle.article.copy(timestamp = timestamp))
        } catch (ex: IllegalArgumentException) {
            return null
        }
        val deFormattedElements = deFormatElements(id, uiArticle.fullContent.elements)
        val numMinutesRead = try {
            calculateReadMinutes(uiArticle.fullContent.elements)
        } catch (ex: BigMinutesReadException) {
            Timber.d("Article's content is too big")
            return null
        }
        val article = uiArticle.article.copy(id = id, numMinutesRead = numMinutesRead, timestamp = timestamp, categoryIds = getCategoryIDs(uiArticle.category))
        return Pair(article, deFormattedElements)
    }

    //This maybe moved later, also it applies for getExpectedElements()
    suspend fun uploadElements(id: articleId, elements: List<Element>): Boolean {
        val result = contentDataSource.addContent(id, elements)
        if (result != null && result.succeeded) {
            return (result as Result.Success).data
        }
        return false
    }

    private suspend fun deFormatElements(id: articleId, elements: List<BaseElement>): List<Element> {
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

    private fun calculateReadMinutes(elements: List<BaseElement>): Int {
        var imagesCount = 0
        val elementWords = mutableListOf<String>()
        for (element in elements) {
            if (element.isTextElement) {
                val words = (element as Element).text?.split(" ")!!.let { list -> list.filterNot { it.isBlank() } }
                elementWords.addAll(words)
            }
            if (element.isImageElement) imagesCount++
        }
        //As per some statistics, Avg. time of reading 1000 word is 3.3 min
        val numMinutesRead = elementWords.size.toDouble().div(1000).times(3.3).roundToLong()
            .also { if (it > 30) throw BigMinutesReadException()}
        return numMinutesRead.toInt() + imagesCount.times(0.2).roundToInt()
    }

    private fun getCategoryIDs(categories: List<Category>): List<String>{
        val categoryIds = mutableListOf<String>()
        for (cat in categories) categoryIds.add(cat.id)
        return categoryIds
    }
}
