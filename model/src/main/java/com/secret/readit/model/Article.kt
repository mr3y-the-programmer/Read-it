/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.model

typealias articleId = String

data class Article(val id: articleId,
                   val title: String,
                   val publisherID: publisherId,
                   val numMinutesRead: Int,
                   val timestamp: Long,
                   val numOfAppreciate: Int = 0,
                   val numOfDisagree: Int = 0,
                   val categoryIds: List<String>)
//Pseudo code
// TODO 1: Implement a new model called UiArticle
// TODO 2: Refactor each field in Article model
// TODO 3: make a separate dataSource in articles package
// TODO 4: Update ArticlesRepo and ArticlesRepoTest
// TODO 5: Update Formatter to format article into corresponding UiArticle
// TODO 6: Update TestData
// TODO 7: Update CustomIDHandler and new function for getting comment ID
// TODO 8: update domain classes to return UiArticle instead of Article
// TODO 9: check if there's updates need to be done on console side and check all refactors goes properly

//TODO (Optional): remove comments field