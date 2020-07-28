/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.secret.readit.model.Article

internal class NormalizeHelper {

    /**
     * convert firestore's Article data model to our local model [Article]
     */
    fun getNormalizedArticles(snapshot: QuerySnapshot): List<Article> {
        val allArticles = snapshot.toObjects(Article::class.java)
        return filterNullArticles(allArticles)
    }

    /**
     * Same but for Single [Article]
     */
    fun getNormalizedArticle(snapshot: DocumentSnapshot): Article? {
        return snapshot.toObject(Article::class.java)
    }

    /**
     * if there's a null articles returned remove them
     */
    private fun filterNullArticles(articles: List<Article?>): List<Article> {
        val nonNullArticles = mutableListOf<Article>()
        if (articles.isNotEmpty()) {
            for (article in articles) {
                val nonNullArticle = article ?: continue
                nonNullArticles.add(nonNullArticle)
            }
        }
        return nonNullArticles
    }
}
