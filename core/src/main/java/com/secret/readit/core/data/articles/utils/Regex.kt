/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles.utils

/**
 * For more information about regex check:
 * 1-"https://howtodoinjava.com/regex/word-boundary-starts-ends-with/#match_at_end_of_content"
 * 2-test & validate: "https://www.freeformatter.com/java-regex-tester.html#ad-output"
 * 3-android reference: "https://developer.android.com/reference/java/util/regex/Pattern#summary-of-regular-expression-constructs"
 */
private const val MULTI_LINE_REGEX = "(?m)" // This make the regex match the start/end of each line
private const val START_REGEX = "^"
private const val END_REGEX = "$"
private const val ALL_CHARS = ".+[a-zA-Z]."
private const val ONE_OR_MORE_TIME = "+"
private const val SINGLE_CHAR_EXCEPT_LINE_SEPARATOR = "."

private const val BULLET_POINT_REGEX = "~"
const val COMPLETE_BULLET_POINT_REGEX = "$MULTI_LINE_REGEX$START_REGEX$BULLET_POINT_REGEX$ALL_CHARS$ONE_OR_MORE_TIME" +
    "$MULTI_LINE_REGEX$SINGLE_CHAR_EXCEPT_LINE_SEPARATOR$BULLET_POINT_REGEX$END_REGEX"

private const val QUOTE_REGEX = ">"
const val COMPLETE_QUOTE_REGEX = "$MULTI_LINE_REGEX$START_REGEX$QUOTE_REGEX$ALL_CHARS$ONE_OR_MORE_TIME" +
    "$MULTI_LINE_REGEX$SINGLE_CHAR_EXCEPT_LINE_SEPARATOR$QUOTE_REGEX$END_REGEX"

private const val CODE_REGEX = "`"
const val COMPLETE_CODE_REGEX = "$MULTI_LINE_REGEX$START_REGEX$CODE_REGEX$ALL_CHARS$ONE_OR_MORE_TIME" +
    "$MULTI_LINE_REGEX$SINGLE_CHAR_EXCEPT_LINE_SEPARATOR$CODE_REGEX$END_REGEX"

private const val STRIKE_THROUGH_REGEX = "-"
const val COMPLETE_STRIKE_THROUGH_REGEX = "$MULTI_LINE_REGEX$START_REGEX$STRIKE_THROUGH_REGEX$ALL_CHARS$ONE_OR_MORE_TIME" +
    "$MULTI_LINE_REGEX$SINGLE_CHAR_EXCEPT_LINE_SEPARATOR$STRIKE_THROUGH_REGEX$END_REGEX"

const val THE_COMPLETE_REGEX = "$COMPLETE_BULLET_POINT_REGEX|$COMPLETE_CODE_REGEX|$COMPLETE_QUOTE_REGEX|$COMPLETE_STRIKE_THROUGH_REGEX"
