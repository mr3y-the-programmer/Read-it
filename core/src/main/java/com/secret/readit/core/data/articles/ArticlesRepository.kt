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
     */
    suspend fun getNewArticles(limit: Int = 0): List<Article> {
        var formattedArticles = mutableListOf<Article>()
        val articlesResult = articlesDataSource.getArticles()

        if (articlesResult.succeeded) {
            val articles = (articlesResult as Result.Success).data
            formattedArticles = formatArticles(articles)
        }
        return formattedArticles
    }

    suspend fun getArticle(id: articleId): Article{
        var formattedArticle = getEmptyArticle()
        val articleResult = articlesDataSource.getArticle(id)

        if (articleResult.succeeded){
            val article = (articleResult as Result.Success).data
            formattedArticle = formatArticle(article)
        }
        return formattedArticle
    }

    /**
     * format articles in expected format for consumers
     */
    private fun formatArticles(articles: List<Article>): MutableList<Article> {
        val formattedArticles = mutableListOf<Article>()
        for (article in articles) {
            var parsedArticle = article
            val formattedElements = formatContent(article.content)
            parsedArticle = parsedArticle.copy(content = Content(formattedElements))
            formattedArticles += parsedArticle
        }
        return formattedArticles
    }

    /**
     * format articles in expected format for consumers
     */
    private fun formatArticle(article: Article): Article {
        var parsedArticle = article
        val formattedElements = formatContent(article.content)
        parsedArticle = parsedArticle.copy(content = Content(formattedElements))

        return parsedArticle
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

    private fun getEmptyArticle(): Article{
        val publisher = Publisher("", "", "", memberSince = -1)
        return Article("", "", Content(emptyList()), publisher, 0, 0, emptyList(), category = emptyList())
    }
}
