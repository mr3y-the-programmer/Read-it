/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles

import com.secret.readit.core.data.articles.utils.Parser
import com.secret.readit.core.result.Result
import com.secret.readit.core.result.succeeded
import com.secret.readit.model.*
import javax.inject.Inject

/**
 * Single Source Of Truth for articles data, Any consumers should consume from it not from data sources directly.
 *
 * Rule: -forward actions to dataSource when needed(i.e: loading new data) And to normalize data in expected format for consumers
 */
// TODO: update repository to use Flows on results
class ArticlesRepository @Inject constructor(
    private val articlesDataSource: ArticlesDataSource,
    private val parser: Parser
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
     * @return true on success, false on data source failure like: No Internet connection
     */
    suspend fun addArticle(article: Article): Boolean{
        var successful = false
        val deFormattedElements = deFormatElements(article.content.elements)

        val result = articlesDataSource.addArticle(article.copy(content = Content(deFormattedElements)))
        if(result.succeeded){
            successful = (result as Result.Success).data
        }
        return successful
    }

    private fun deFormatElements(elements: List<Element>): List<Element>{
        val deFormattedElements = mutableListOf<Element>()
        val deFormattedStrings = mutableListOf<String>()
        for (element in elements){
            if (element.imageUri == null) { //reverse only text
                val deFormattedString = parser.reverseParse(element)
                deFormattedStrings += deFormattedString
            }
            deFormattedElements += element
        }
        return deFormattedElements
    }
    /**
     * format articles in expected format for consumers
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T> formatArticles(result: Result<T>, singleItem: Boolean = false): MutableList<Article> {
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

    private fun formatContent(content: Content): MutableList<Element> {
        val formattedElements = mutableListOf<Element>()
        for (element in content.elements) {
            var parsedElement = element
            if (element.imageUri == null) { // parse text only
                parsedElement = parser.parse(element.text!!)
            }
            formattedElements += parsedElement
        }
        return formattedElements
    }

    private fun getEmptyArticle(): Article {
        val publisher = Publisher("", "", "", memberSince = -1)
        return Article("", "", Content(emptyList()), publisher, 0, 0, emptyList(), category = emptyList())
    }
}
