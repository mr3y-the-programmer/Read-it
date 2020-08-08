/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.domain.search

import com.algolia.search.client.ClientSearch
import com.algolia.search.client.Index
import com.algolia.search.dsl.*
import com.algolia.search.model.IndexName
import com.secret.readit.core.domain.FlowUseCase
import com.secret.readit.core.domain.search.Searchable.Article
import com.secret.readit.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.serialization.json.content
import kotlinx.serialization.json.long
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

/**
 * SearchArticle UseCase which takes [SearchArticleParams] as param and
 * return the Flow of Articles that match this search
 */
class SearchArticle @Inject constructor(private val searchClient: ClientSearch): FlowUseCase<SearchArticleParams, Article>(){

    override suspend fun execute(parameters: SearchArticleParams): Flow<Article> {
        val consumedResult = mutableListOf<Article>()
        val articleIndex = configureIndex(parameters)
        val query = query(parameters.query) { hitsPerPage = 50 }
        articleIndex.search(query).hits.forEach {
            val title = it.highlightResult.content["value"]!!.content
            val content = it.snippetResult.content["value"]!!.content //Snippet includes also highlighting
            val publisherName = it["pubName"]!!.content //display the pub of article
            val timestamp = ZonedDateTime.ofInstant(Instant.ofEpochMilli(it["timestamp"]!!.long), ZoneId.systemDefault()).year
            consumedResult.add(Article(title, content, publisherName, timestamp))
        }
        return consumedResult.asFlow()
    }

    private suspend fun configureIndex(parameters: SearchArticleParams): Index {
        //This should be moved to cloud function we create but with ADMIN_API
        return searchClient.initIndex(indexName).apply {
            val settings = settings { //IndexTime
                searchableAttributes {
                    +Unordered("title") //Because title can contain words like: "the"..etc in first so it is better to make it Unordered
                    +"content"
                }
                attributesForFaceting {
                    if (parameters.categories.isNotEmpty()) +Searchable("categoryIds") //benefit from search_for_facet_value feature
                    if (parameters.since.isAfter(ZonedDateTime.parse(FIRST_ARTICLE_CREATED))) +"timestamp"
                }
                highlightPreTag = "<b>" //Bolden the highlighted found text
                highlightPostTag = "</b>"
                attributesToSnippet {
                    +"content" //snippet content with max engine default "10"
                }
                restrictHighlightAndSnippetArrays = true //Without this, all words regardless matched query or not will be highlighted and snippet
                ranking {
                    +Desc("numOfAppreciate")
                    +Asc("numOfDisagree")
                }
            }
            setSettings(settings)
        }
    }

    companion object {
        //TODO: configure those properly or fetch them from RemoteConfig
        val indexName = IndexName("articles")
        const val FIRST_ARTICLE_CREATED = "2020-09-15T0:0:0.00+02:00[Africa/Cairo]"
    }
}
//TODO: remove articleIds field from category model
/**
 * takes User's parameters to make a search request , can filter User search by [categories] or [since]
 */
data class SearchArticleParams(val query: String, //This is the only required field to make a search request
                               val categories: List<Category> = emptyList(),
                               val since: ZonedDateTime = ZonedDateTime.parse(SearchArticle.FIRST_ARTICLE_CREATED))