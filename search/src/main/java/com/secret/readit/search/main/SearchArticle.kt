/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.search.main

import com.algolia.search.client.ClientSearch
import com.algolia.search.client.Index
import com.algolia.search.dsl.attributesForFaceting
import com.algolia.search.dsl.attributesToSnippet
import com.algolia.search.dsl.query
import com.algolia.search.dsl.ranking
import com.algolia.search.dsl.searchableAttributes
import com.algolia.search.dsl.settings
import com.algolia.search.model.IndexName
import com.secret.domain.FlowUseCase
import com.secret.readit.model.Category
import com.secret.readit.search.main.Searchable.SearchableArticle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.serialization.json.content
import kotlinx.serialization.json.long
import javax.inject.Inject

/**
 * SearchArticle UseCase which takes [SearchArticleParams] as param and
 * return the Flow of Articles that match this search
 */
class SearchArticle @Inject constructor(private val searchClient: ClientSearch) : FlowUseCase<SearchArticleParams, SearchableArticle>() {

    override suspend fun execute(parameters: SearchArticleParams): Flow<SearchableArticle> {
        val consumedResult = mutableListOf<SearchableArticle>()
        val articleIndex = configureIndex()
        val query = query(parameters.query) { hitsPerPage = 50 }
        articleIndex.search(query).hits.forEach {
            val title = it.highlightResult.content["value"]!!.content
            val content = it.snippetResult.content["value"]!!.content // Snippet includes also highlighting
            val publisherName = it["pubName"]!!.content // display the pub of article
            val timestamp = it["timestamp"]!!.long
            consumedResult.add(SearchableArticle(title, content, publisherName, timestamp))
        }
        return consumedResult.asFlow()
    }

    private suspend fun configureIndex(): Index {
        // This should be moved to cloud function we create but with ADMIN_API
        return searchClient.initIndex(indexName).apply {
            val settings = settings { // IndexTime
                searchableAttributes {
                    +Unordered("title") // Because title can contain words like: "the"..etc in first so it is better to make it Unordered
                    +"content"
                }
                attributesForFaceting {
                    +Searchable("categoryIds") // benefit from search_for_facet_value feature
                    +"timestamp"
                }
                highlightPreTag = "<b>" // Bolden the highlighted found text
                highlightPostTag = "</b>"
                attributesToSnippet {
                    +"content" // snippet content with max engine default "10"
                }
                restrictHighlightAndSnippetArrays = true // Without this, all words regardless matched query or not will be highlighted and snippet
                ranking {
                    +Desc("numOfAppreciate")
                    +Asc("numOfDisagree")
                }
            }
            setSettings(settings)
        }
    }

    companion object {
        val indexName = IndexName("articles")
    }
}
// TODO: remove articleIds field from category model
/**
 * takes User's parameters to make a search request , can filter User search by [categories] or [since]
 */
data class SearchArticleParams(
    val query: String, // This is the only required field to make a search request
    val categories: List<Category> = emptyList(),
    val since: Long = 0L
)
