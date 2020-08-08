/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.domain.search

import com.algolia.search.client.ClientSearch
import com.algolia.search.client.Index
import com.algolia.search.dsl.*
import com.algolia.search.model.IndexName
import com.secret.domain.FlowUseCase
import com.secret.domain.search.Searchable.Publisher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.serialization.json.content
import kotlinx.serialization.json.long
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

/**
 * Identical to [SearchArticle] but for publishers
 */
class SearchPublisher @Inject constructor(private val searchClient: ClientSearch) : FlowUseCase<SearchPublisherParams, Publisher>() {

    override suspend fun execute(parameters: SearchPublisherParams): Flow<Publisher> {
        val consumedResult = mutableListOf<Publisher>()
        val pubIndex = configureIndex(parameters)
        val query = query(parameters.query) { hitsPerPage = 50 }
        pubIndex.search(query).hits.forEach {
            val name = it.highlightResult.content["value"]!!.content
            val email = it.snippetResult.content["value"]!!.content
            val sinceYear = ZonedDateTime.ofInstant(Instant.ofEpochMilli(it["memberSince"]!!.long), ZoneId.systemDefault()).year
            // With Algolia's provided widgets and glide we can use this url directly as Img, See: https://discourse.algolia.com/t/how-do-i-add-an-image-to-an-object-in-algolia/8398
            val profileImg = it["profileImgUri"]!!.content
            consumedResult.add(Publisher(name, email, sinceYear, profileImg))
        }
        return consumedResult.asFlow()
    }

    private suspend fun configureIndex(parameters: SearchPublisherParams): Index {
        // This should be moved to cloud function we create but with ADMIN_API
        return searchClient.initIndex(index).apply {
            val settings = settings { // IndexTime
                searchableAttributes {
                    +Unordered("name")
                    +"emailAddress"
                }
                attributesForFaceting {
                    if (parameters.since.isAfter(FIRST_ACCOUNT_CREATED)) +"memberSince"
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
        val FIRST_ACCOUNT_CREATED: ZonedDateTime = ZonedDateTime.of(2020, 9, 15, 0, 0, 0, 0, ZoneId.systemDefault())
    }
}

/**
 * Similar to [SearchArticleParams]
 */
data class SearchPublisherParams(
    val query: String, // The only required field to make a search request
    val since: ZonedDateTime = SearchPublisher.FIRST_ACCOUNT_CREATED
)
