/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.search.main

import com.algolia.search.client.ClientSearch
import com.algolia.search.client.Index
import com.algolia.search.dsl.*
import com.algolia.search.model.IndexName
import com.secret.domain.FlowUseCase
import com.secret.readit.search.main.Searchable.SearchablePublisher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.serialization.json.content
import kotlinx.serialization.json.long
import javax.inject.Inject

/**
 * Identical to [SearchArticle] but for publishers
 */
class SearchPublisher @Inject constructor(private val searchClient: ClientSearch) : FlowUseCase<SearchPublisherParams, SearchablePublisher>() {

    override suspend fun execute(parameters: SearchPublisherParams): Flow<SearchablePublisher> {
        val consumedResult = mutableListOf<SearchablePublisher>()
        val pubIndex = configureIndex()
        val query = query(parameters.query) { hitsPerPage = 50 }
        pubIndex.search(query).hits.forEach {
            val name = it.highlightResult.content["value"]!!.content
            val email = it.snippetResult.content["value"]!!.content
            val sinceYear = it["memberSince"]!!.long
            // With Algolia's provided widgets and glide we can use this url directly as Img, See: https://discourse.algolia.com/t/how-do-i-add-an-image-to-an-object-in-algolia/8398
            val profileImg = it["profileImgUri"]!!.content
            consumedResult.add(SearchablePublisher(name, email, sinceYear, profileImg))
        }
        return consumedResult.asFlow()
    }

    private suspend fun configureIndex(): Index {
        // This should be moved to cloud function we create but with ADMIN_API
        return searchClient.initIndex(index).apply {
            val settings = settings { // IndexTime
                searchableAttributes {
                    +Unordered("name")
                    +"emailAddress"
                }
                attributesForFaceting {
                     +"memberSince"
                }
                highlightPreTag = "<b>" // Bolden the highlighted found text
                highlightPostTag = "</b>"
                attributesToSnippet {
                    +"emailAddress" // snippet emailAddress with max engine default "10"
                }
                restrictHighlightAndSnippetArrays = true // Without this, all words regardless matched query or not will be highlighted and snippet
                ranking {
                    +Desc("numOfFollowers")
                }
            }
            setSettings(settings)
        }
    }

    companion object {
        val index = IndexName("publishers")
    }
}

/**
 * Similar to [SearchArticleParams]
 */
data class SearchPublisherParams(
    val query: String, // The only required field to make a search request
    val since: Long = 0L
)
