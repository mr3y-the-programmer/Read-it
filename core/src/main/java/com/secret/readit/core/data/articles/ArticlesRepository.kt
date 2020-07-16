/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles

import android.net.Uri
import com.secret.readit.core.data.utils.CustomIDHandler
import com.secret.readit.core.data.articles.utils.Parser
import com.secret.readit.core.data.shared.StorageRepository
import com.secret.readit.core.result.Result
import com.secret.readit.core.result.succeeded
import com.secret.readit.core.uimodels.ImageUiElement
import com.secret.readit.model.*
import java.lang.IllegalArgumentException
import javax.inject.Inject

/**
 * Single Source Of Truth for articles data, Any consumers should consume from it not from data sources directly.
 *
 * Rule: -forward actions to dataSource when needed(i.e: loading new data) And to normalize data in expected format for consumers
 */
// TODO: update repository to use Flows on results
class ArticlesRepository @Inject constructor(
    private val articlesDataSource: ArticlesDataSource,
    private val storageRepo: StorageRepository,
    private val parser: Parser = Parser,
    private val idHandler: CustomIDHandler = CustomIDHandler()
) {

    /**
     * For now it is not clear how we will paginate the data, So These APIs are going to be modified
     * when designing/building the domain layer
     *
     * @return valid articles or empty list if data source failed
     */
    suspend fun getNewArticles(limit: Int = 0): List<Article> {
        val articlesResult = articlesDataSource.getArticles()
        return formatArticles(articlesResult)
    }

    /**
     * get Article with this specified id
     *
     * @return valid article or empty article if data source failed
     */
    suspend fun getArticle(id: articleId): Article {
        val articleResult = articlesDataSource.getArticle(id)
        val formattedArticle = formatArticles(articleResult, true)
        if (formattedArticle.isNullOrEmpty()) {
            return getEmptyArticle()
        }
        return formattedArticle[0]
    }

    /**
     * Publish this article, add it to firestore
     *
     * @return true on success, false on data source failure like: No Internet connection or adding invalid article
     */
    suspend fun addArticle(article: Article): Boolean {
        var successful = false
        val deFormattedElements = deFormatElements(article.content.elements)
        var id = ""
        try {
            id = idHandler.getID(article)
        } catch (ex: IllegalArgumentException) {
            return false
        }

        val result = articlesDataSource.addArticle(article.copy(id = id, content = Content(deFormattedElements)))
        if (result != null && result.succeeded) {
            successful = (result as Result.Success).data
        }
        return successful
    }

    /**
     * update bookmark state on firestore
     *
     * @return true on success, false on data source failure like: No Internet connection or invalid article
     */
    suspend fun toggleBookmark(article: Article, bookmark: Boolean): Boolean {
        var successful = false
        var id = ""
        try {
            id = idHandler.getID(article)
        } catch (ex: IllegalArgumentException) {
            return false
        }

        val result = articlesDataSource.bookmark(id, bookmark)
        if (result.succeeded) {
            successful = (result as Result.Success).data
        }
        return successful
    }

    /**
     * format articles in expected format for consumers
     */
    @Suppress("UNCHECKED_CAST")
    private suspend fun <T> formatArticles(result: Result<T>, singleItem: Boolean = false): MutableList<Article> {
        val formattedArticles = mutableListOf<Article>()
        if (result.succeeded) {
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

    private suspend fun formatContent(content: Content): MutableList<BaseElement> {
        val formattedElements = mutableListOf<BaseElement>()
        for (baseElement in content.elements) {
            var firestoreElement = (baseElement as Element)
            if (firestoreElement.imageUri == null) { // parse text only
                firestoreElement = parser.parse(baseElement.text!!)
                formattedElements += firestoreElement //In this case UiElement is the same as firestoreElement
            }
            if (firestoreElement.imageUri != null) {
                val imgUri = Uri.parse(firestoreElement.imageUri)
                val bitmap = storageRepo.downloadImg(imgUri, PLACE_HOLDER_URL)
                formattedElements += ImageUiElement(bitmap)
            }
        }
        return formattedElements
    }

    private fun getEmptyArticle(): Article {
        val publisher = Publisher("", "", "", memberSince = -1)
        return Article("", "", Content(emptyList()), publisher, 0, 0, emptyList(), category = emptyList())
    }

    private fun deFormatElements(elements: List<Element>): List<Element> {
        val deFormattedElements = mutableListOf<Element>()
        for (element in elements) {
            var deFormattedElement = element
            var deFormattedString = ""
            if (element.imageUri == null) { // reverse only text
                deFormattedString = parser.reverseParse(element)
                deFormattedElement = deFormattedElement.copy(text = deFormattedString)
            }
            /*if (element.imageUri != null) {

            }*/
            deFormattedElements += deFormattedElement
        }
        return deFormattedElements
    }

    companion object {
        const val PLACE_HOLDER_URL = "https://firebasestorage.googleapis.com/v0/b/read-it-b9c8b.appspot.com" +
                "/o/articles%2Fplace_holder_image.png?alt=media&token=fd6b444e-0115-4f40-8b8d-f6deaf238179"
    }
}
