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

    fun parse(string: String): Element{
        val completePattern = Pattern.compile(THE_COMPLETE_REGEX)
        val matcher = completePattern.matcher(string)

        var markup = Markup(MarkupType.TEXT, 0, string.length) //Default
        var parsedString = string //Default

        if (matcher.find()) {
            val startOfMatch = matcher.start()
            val endOfMatch = matcher.end()
            val quotePattern = Pattern.compile(COMPLETE_QUOTE_REGEX)
            val bulletPointPattern = Pattern.compile(COMPLETE_BULLET_POINT_REGEX)

            markup = if (quotePattern.matcher(string).matches()) {
                markup.copy(MarkupType.QUOTE)
            } else if (bulletPointPattern.matcher(string).matches()) {
                markup.copy(MarkupType.BulletPoints)
            } else {
                markup.copy(MarkupType.CODE)
            }
            parsedString = string.substring(1, string.length - 1)
            markup = markup.copy(start = startOfMatch, end = endOfMatch)
        }
        return Element(text = parsedString, markup = markup, elements = emptyList())
    }
}