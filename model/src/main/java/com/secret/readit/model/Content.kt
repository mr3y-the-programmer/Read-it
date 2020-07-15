/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.model

/**
 * Wrapper around all elements whether they are (image,text...etc)
 */
data class Content(val elements: List<BaseElement>)


/**
 * Separate image Element constructor from text Element constructor to enforce callers choose one of them,
 * This solution is Easy/scalable so we can add more types if we need in the future.
 *
 * But has one caveat, that need just a little caution from callers:
 * -main(private) constructor can be accessed through copy() fun.
 * It is not a big problem In our case since most of copy() Usage will be in tests
 *
 * Another solution we can do here Using: sealed classes
 */
@Suppress("DataClassPrivateConstructor")
data class Element private constructor(
    val text: String? = null,
    val markup: Markup? = null,
    val imageUri: String? = null): BaseElement() {

    //One that should be called when inserting text
    constructor(text: String, markup: Markup): this(text, markup, null)

    //One that should be called when inserting image
    constructor(imageUri: String): this(null, null, imageUri)
}


data class Markup(val type: MarkupType,
                  val start: Int,
                  val end: Int)


enum class MarkupType{
    CODE,
    QUOTE,
    BulletPoints,
    TEXT
}