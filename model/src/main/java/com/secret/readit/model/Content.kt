/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.model

data class Content(val images: List<Byte>,
                   val elements: List<Element>)


data class Element(val text: String,
                   val markups: List<Markup>,
                   val elements: List<Element>)


data class Markup(val type: MarkupType,
                  val start: Int,
                  val end: Int)


enum class MarkupType{
    CODE,
    QUOTE,
    BulletPoints
}