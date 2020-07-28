/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import kotlin.IllegalArgumentException

class CustomIDHandlerTest {

    // Object under test
    private lateinit var customIDHandler: CustomIDHandler

    @Before
    fun setUp() {
        customIDHandler = CustomIDHandler()
    }

    @Test
    fun inputArticle2_ReturnValidId() = runSuccessTest({ customIDHandler.getID(TestData.article2) }, "89479892-2pub-arti")

    @Test
    fun inputArticle1_ReturnValidId() = runSuccessTest({ customIDHandler.getID(TestData.article1) }, "43259253-1pub-arti")

    @Test(expected = IllegalArgumentException::class)
    fun inputInvalidArticle_ThrowsException() { customIDHandler.getID(TestData.emptyArticle) /* When entering Invalid article */ }

    @Test
    fun inputCategory1_ReturnValidId() = runSuccessTest({ customIDHandler.getID(TestData.category1) }, "pPt-Programming-ego")

    @Test(expected = IllegalArgumentException::class)
    fun inputInvalidCategory_ThrowsException() { customIDHandler.getID(TestData.emptyCategory) /* When entering Invalid category */ }

    @Test
    fun inputComment1_ReturnValidId() = runSuccessTest({ customIDHandler.getID(TestData.comment1) }, "1pub-64334")

    @Test(expected = IllegalArgumentException::class)
    fun inputInvalidComment_ThrowsException() { customIDHandler.getID(TestData.emptyComment) /*When entering Invalid comment*/ }

    private fun runSuccessTest(funUnderTest: CustomIDHandler.() -> String, expected: String) {
        val result = customIDHandler.funUnderTest() // When entering valid object

        assertThat(result).isEqualTo(expected) // Assert it matches our expectations
    }
}
