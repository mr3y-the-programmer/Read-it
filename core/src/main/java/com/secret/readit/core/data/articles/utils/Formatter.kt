/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles.utils

import android.net.Uri
import androidx.paging.PagingData
import androidx.paging.map
import com.secret.readit.core.data.articles.ArticlesRepository
import com.secret.readit.core.data.articles.content.ContentDataSource
import com.secret.readit.core.data.categories.CategoryRepository
import com.secret.readit.core.data.publisher.PublisherRepository
import com.secret.readit.core.data.shared.StorageRepository
import com.secret.readit.core.data.utils.CustomIDHandler
import com.secret.readit.core.data.utils.isImageElement
import com.secret.readit.core.data.utils.isTextElement
import com.secret.readit.core.paging.ArticleWithContent
import com.secret.readit.core.result.Result
import com.secret.readit.core.result.succeeded
import com.secret.readit.core.uimodels.ImageUiElement
import com.secret.readit.core.uimodels.UiArticle
import com.secret.readit.core.uimodels.UiComment
import com.secret.readit.model.*
import timber.log.Timber
import java.time.Instant
import javax.inject.Inject
import kotlin.IllegalArgumentException
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

    /** format articles in expected format for consumers*/
    suspend fun formatArticles(page: PagingData<ArticleWithContent>): PagingData<UiArticle>{
        return page.map {
            val article = it.first
            val content = it.second
            val publisher = pubRepo.getPublisherInfo(article.publisherID)
            val initialContent = formatContent(content)
            val categories = categoryRepo.getCategories(article.categoryIds)
            UiArticle(article, publisher = publisher, category = categories, initialContent = Content(initialContent))
        }
    }

    // Handle formatting elements
    @Suppress("UNCHECKED_CAST")
    suspend fun formatContent(content: Content): List<BaseElement> {
        val elements = content.elements as List<Element>
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

    suspend fun formatComments(result: Result<List<Comment>>) = formatCommentsOrReplies(result)

    // Return the parent comment but with formatting its replies
    suspend fun formatReplies(result: Result<List<Comment>>, parentComment: UiComment) = parentComment.copy(replies = formatCommentsOrReplies(result))

    private suspend fun formatCommentsOrReplies(result: Result<List<Comment>>): List<UiComment> {
        val formattedComments = mutableListOf<UiComment>()
        if (result != null && result.succeeded) {
            val comments = (result as Result.Success).data
            for (comment in comments) {
                val pub = pubRepo.getPublisherInfo(comment.publisherID)
                formattedComments += UiComment(comment, pub)
            }
        }
        return formattedComments
    }

    // deFormatting section
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

    // FIXME: Architecture drift, This should be moved later, also this applies for getExpectedElements()
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
        // As per some statistics, Avg. time of reading 1000 word is 3.3 min
        val numMinutesRead = elementWords.size.toDouble().div(1000).times(3.3).roundToLong()
            .also { if (it > 30) throw BigMinutesReadException() }
        return numMinutesRead.toInt() + imagesCount.times(0.2).roundToInt()
    }

    private fun getCategoryIDs(categories: List<Category>): List<String> {
        val categoryIds = mutableListOf<String>()
        for (cat in categories) categoryIds.add(cat.id)
        return categoryIds
    }

    fun deFormatComment(comment: UiComment): Comment? {
        val now = Instant.now().toEpochMilli()
        val pubID = comment.pub.publisher.id
        val commentId = try {
            idHandler.getID(comment.comment.copy(timestamp = now, publisherID = pubID))
        } catch (ex: IllegalArgumentException) {
            Timber.d("Cannot get an id for comment: $comment")
            return null
        }
        return comment.comment.copy(id = commentId, publisherID = pubID, timestamp = now, repliesIds = emptyList())
    }
}
