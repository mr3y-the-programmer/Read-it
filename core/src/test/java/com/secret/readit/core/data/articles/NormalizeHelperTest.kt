/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles

import com.google.firebase.firestore.QuerySnapshot
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.secret.readit.model.Article
import org.junit.Assert.assertEquals
import org.junit.Test

class NormalizeHelperTest {

    private lateinit var querySnapshot: QuerySnapshot

    @Test
    fun getArticles_removeNullOnes() {
        // GIVEN a null-free elements list
        val expected = listOf(TestData.article1)

        querySnapshot = mock {
            on(it.toObjects(Article::class.java)).doReturn(listOf(TestData.article1, null, null))
        }

        val result = NormalizeHelper().getNormalizedArticles(querySnapshot)

        // Assert, it equals our returned list
        assertEquals(expected, result)
    }
}
