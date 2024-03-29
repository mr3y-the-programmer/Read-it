/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles.utils

import com.secret.readit.model.Element
import com.secret.readit.model.Markup
import com.secret.readit.model.MarkupType
import java.util.regex.Pattern

/**
 * Handle Parsing Element's text into markups and vice-versa.
 *
 * This is inspired by TextStylingKotlin sample, Thanks For: Florina Muntunascu, Jeremy Walker and others contributors
 */
object Parser {

    fun parse(string: String): Element {
        val completePattern = Pattern.compile(THE_COMPLETE_REGEX)
        val matcher = completePattern.matcher(string)

        var markup = Markup(MarkupType.TEXT, 0, string.length) // Default
        var parsedString = string // Default

        if (matcher.find()) {
            val startOfMatch = matcher.start()
            val endOfMatch = matcher.end()
            val quotePattern = Pattern.compile(COMPLETE_QUOTE_REGEX)
            val bulletPointPattern = Pattern.compile(COMPLETE_BULLET_POINT_REGEX)
            val strikeThroughPattern = Pattern.compile(COMPLETE_STRIKE_THROUGH_REGEX)

            markup = if (quotePattern.matcher(string).matches()) {
                markup.copy(MarkupType.QUOTE)
            } else if (bulletPointPattern.matcher(string).matches()) {
                markup.copy(MarkupType.BulletPoints)
            } else if (strikeThroughPattern.matcher(string).matches()) {
                markup.copy(MarkupType.StrikeThrough)
            } else {
                markup.copy(MarkupType.CODE)
            }
            parsedString = string.substring(1, string.length - 1)
            markup = markup.copy(start = startOfMatch, end = endOfMatch)
        }
        return Element(text = parsedString, markup = markup)
    }

    /**
     * reverse markups into regular string in order to be stored in firestore
     */
    fun reverseParse(element: Element): String {
        return when (element.markup?.type?.ordinal) {
            0 -> "`${element.text}`" // Code block
            1 -> ">${element.text}>" // Quote
            2 -> "~${element.text}~" // bullet points
            3 -> "-${element.text}-" // StrikeThrough
            else -> element.text!! // plain text
        }
    }
}
