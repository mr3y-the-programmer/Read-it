/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.articles.utils

import com.google.common.truth.Truth.assertThat
import com.secret.readit.core.TestData
import com.secret.readit.model.MarkupType
import org.junit.Test

class ParserTest {

    // TODO: refactor

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
    fun strikeThrough_ReturnTheTextParsedCorrectly() {
        // GIVEN strike through element
        val strikeThrough = TestData.strikeThroughElement.text!!

        // When parsing this strikeThrough
        val element = Parser.parse(strikeThrough)

        // Assert it matches our expectations
        assertThat(element.text).isEqualTo(" This is Strike Through -    ")
        assertThat(element.markup?.type).isEqualTo(MarkupType.StrikeThrough)
        assertThat(element.markup?.start).isEqualTo(0)
        assertThat(element.markup?.end).isEqualTo(TestData.strikeThroughElement.text!!.length)
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

    // End of testing parse()
    // Beginning of testing reverseParse()

    @Test
    fun quoteMarkup_ReturnQuotesWithoutMarkup() {
        // GIVEN a parsed multiple line element
        val element = Parser.parse(TestData.multipleLineQuoteElement.text!!)

        // When trying to reverse parsing it
        val string = Parser.reverseParse(element)

        // Assert it matches our expectations
        assertThat(string).isEqualTo(TestData.multipleLineQuoteElement.text!!)
    }

    @Test
    fun codeMarkup_ReturnCodeWithoutMarkup() {
        // GIVEN a parsed code element
        val element = Parser.parse(TestData.codeBlockElement.text!!)

        // When trying to reverse parsing it
        val string = Parser.reverseParse(element)

        // Assert it matches our expectations
        assertThat(string).isEqualTo(TestData.codeBlockElement.text!!)
    }

    @Test
    fun bulletPointsMarkup_ReturnTextWithoutMarkup() {
        // GIVEN a parsed bullet point element
        val element = Parser.parse(TestData.multipleBulletPointElement.text!!)

        // When trying to reverse parsing it
        val string = Parser.reverseParse(element)

        // Assert it matches our expectations
        assertThat(string).isEqualTo(TestData.multipleBulletPointElement.text!!)
    }

    @Test
    fun strikeThroughMarkup_ReturnTheTextWithoutMarkup() {
        // GIVEN strike through element
        val strikeThrough = Parser.parse(TestData.strikeThroughElement.text!!)

        // When trying to reverse parsing it
        val string = Parser.reverseParse(strikeThrough)

        // Assert it matches our expectations
        assertThat(string).isEqualTo(TestData.strikeThroughElement.text!!)
    }

    @Test
    fun plainText_ReturnTheSame() {
        // GIVEN a parsed multiple line text
        val element = Parser.parse(TestData.plaintTextElement.text!!)

        // When trying to reverse parsing it
        val string = Parser.reverseParse(element)

        // Assert it matches our expectations
        assertThat(string).isEqualTo(TestData.plaintTextElement.text!!)
    }
}
