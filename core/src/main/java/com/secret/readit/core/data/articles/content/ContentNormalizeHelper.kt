/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles.content

import com.google.firebase.firestore.QuerySnapshot
import com.secret.readit.model.Element
import com.secret.readit.model.Markup
import com.secret.readit.model.MarkupType

internal class ContentNormalizeHelper {

    fun normalizeToElements(snapshot: QuerySnapshot): List<Element> {
        val normalizedElements = mutableListOf<Element>()
        val firestoreElements = snapshot.toObjects(TransientFirestoreElement::class.java)
        for (ele in firestoreElements) {
            if (ele.imgUri == null && (ele.text != null && ele.markup != null)) { // text element
                val markup = ele.markup
                val type = markup.getValue(TYPE_KEY).convertToEnumValue()
                val start = markup.getValue(START_KEY).toInt()
                val end = markup.getValue(END_KEY).toInt()
                normalizedElements += Element(text = ele.text, markup = Markup(type, start, end))
            }
            if (ele.imgUri != null) normalizedElements.add(Element(imageUri = ele.imgUri)) // Img element
        }
        return normalizedElements
    }

    fun deNormalizeElements(elements: List<Element>): List<TransientFirestoreElement> {
        val deNormalizedElements = mutableListOf<TransientFirestoreElement>()
        for (e in elements) {
            if (e.imageUri == null) {
                val markup = e.markup!!
                val markupMap = mapOf(TYPE_KEY to markup.type.name, START_KEY to markup.start.toString(), END_KEY to markup.end.toString())
                deNormalizedElements += TransientFirestoreElement(text = e.text, markup = markupMap, imgUri = null)
            }
            if (e.imageUri != null) deNormalizedElements.add(TransientFirestoreElement(imgUri = e.imageUri, text = null, markup = null))
        }
        return deNormalizedElements
    }

    private fun String.convertToEnumValue(): MarkupType = when (this) {
        "CODE" -> MarkupType.CODE
        "QUOTE" -> MarkupType.QUOTE
        "BulletPoints" -> MarkupType.BulletPoints
        else -> MarkupType.TEXT
    }

    companion object {
        const val TYPE_KEY = "type"
        const val START_KEY = "start"
        const val END_KEY = "end"
    }
}
