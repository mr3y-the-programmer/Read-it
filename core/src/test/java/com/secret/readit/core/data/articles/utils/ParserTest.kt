/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles.utils

import com.google.common.truth.Truth.assertThat
import com.secret.readit.model.MarkupType
import org.junit.Test

class ParserTest {

    @Test
    fun oneLineQuote_ReturnTextWithoutQuotes() {
        // GIVEN one line quote
        val quote = TestData.oneLineQuoteElement.text!!

        // When parsing this quote
        val element = Parser.parse(quote)

        // Assert it matches our expectations
        assertThat(element.text).isEqualTo(" This is a Quote  ")
        assertThat(element.markup?.type).isEqualTo(MarkupType.QUOTE)
        assertThat(element.markup?.start).isEqualTo(0)
        assertThat(element.markup?.end).isEqualTo(TestData.oneLineQuoteElement.text!!.length)
    }

    @Test
    fun twoLineQuote_ReturnTextWithoutQuotes() {
        // GIVEN multiple line quote
        val quote = TestData.multipleLineQuoteElement.text!!

        // When parsing this quote
        val element = Parser.parse(quote)

        // Assert it matches our expectations
        assertThat(element.text).isEqualTo(
            "      This is twwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwooooooooooooooooooooooo" +
                " Liiinnnnneeeeeeeeeeeeeee Quoooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooote"
        )
        assertThat(element.markup?.type).isEqualTo(MarkupType.QUOTE)
        assertThat(element.markup?.start).isEqualTo(0)
        assertThat(element.markup?.end).isEqualTo(TestData.multipleLineQuoteElement.text!!.length)
    }

    @Test
    fun oneLineBullet_ReturnTextWithoutBullets() {
        // GIVEN one Line bullet
        val bulletPoint = TestData.bulletPointElement.text!!

        // When parsing this bullet
        val element = Parser.parse(bulletPoint)

        // Assert it matches our expectations
        assertThat(element.text).isEqualTo(" This is One Bullet Point              ")
        assertThat(element.markup?.type).isEqualTo(MarkupType.BulletPoints)
        assertThat(element.markup?.start).isEqualTo(0)
        assertThat(element.markup?.end).isEqualTo(TestData.bulletPointElement.text!!.length)
    }

    @Test
    fun multipleLineBullet_ReturnTextWithoutBullets() {
        // GIVEN multiple Line bullet
        val bulletPoint = TestData.multipleBulletPointElement.text!!

        // When parsing this bullet
        val element = Parser.parse(bulletPoint)

        // Assert it matches our expectations
        assertThat(element.text).isEqualTo(
            "                               THis is Multiple                  Bullet" +
                "                   Point        "
        )
        assertThat(element.markup?.type).isEqualTo(MarkupType.BulletPoints)
        assertThat(element.markup?.start).isEqualTo(0)
        assertThat(element.markup?.end).isEqualTo(TestData.multipleBulletPointElement.text!!.length)
    }

    @Test
    fun codeBlock_ReturnTheCode() {
        // GIVEN code block element
        val codeBlock = TestData.codeBlockElement.text!!

        // When parsing this code block
        val element = Parser.parse(codeBlock)

        // Assert it matches our expectations
        assertThat(element.text).isEqualTo(" THis is code in Kotllllllllllllllllllin And These are some codes on Javaaaaaaaaaaaaaaaa")
        assertThat(element.markup?.type).isEqualTo(MarkupType.CODE)
        assertThat(element.markup?.start).isEqualTo(0)
        assertThat(element.markup?.end).isEqualTo(TestData.codeBlockElement.text!!.length)
    }

    @Test
    fun plainText_ReturnItWithoutChange() {
        // GIVEN text plain element
        val plainText = TestData.plaintTextElement.text!!

        // When parsing this plain text
        val element = Parser.parse(plainText)

        // Assert it matches our expectations
        assertThat(element.text).isEqualTo("This is just a simple text, No more than it")
        assertThat(element.markup?.type).isEqualTo(MarkupType.TEXT)
        assertThat(element.markup?.start).isEqualTo(0)
        assertThat(element.markup?.end).isEqualTo(TestData.plaintTextElement.text!!.length)
    }
}
