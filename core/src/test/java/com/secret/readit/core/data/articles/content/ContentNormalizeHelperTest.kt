/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles.content

import com.google.firebase.firestore.QuerySnapshot
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.secret.readit.model.Markup
import com.secret.readit.model.MarkupType
import org.junit.Assert.*
import org.junit.Test

class ContentNormalizeHelperTest {

    private val element1 = TransientFirestoreElement("> This is Quote >", mapOf("type" to "QUOTE", "start" to "0", "end" to "17"), null)
    private val element2 = TransientFirestoreElement("` This is CodeBlock `", mapOf("type" to "CODE", "start" to "0", "end" to "21"), null)
    private val element3 = TransientFirestoreElement(null, null, "https://'sxzxvc33434.com/?q/21324354")
    private val transientElements = listOf(element1, element2, element3)

    // Object under test
    private val normalizer = ContentNormalizeHelper()

    @Test
    fun `allOk normalizeSucceed`(){
        val mockedSnapshot = mock<QuerySnapshot> {
            on(it.toObjects(TransientFirestoreElement::class.java)).doReturn(transientElements)
        }
        val elements = normalizer.normalizeToElements(mockedSnapshot)

        assertEquals(elements.size, 3)
        assertEquals(elements[0].text, "> This is Quote >")
        assertEquals(elements[0].markup, Markup(MarkupType.QUOTE, 0, 17))
        assertEquals(elements[1].text, "` This is CodeBlock `")
        assertEquals(elements[1].markup, Markup(MarkupType.CODE, 0, 21))
    }
}