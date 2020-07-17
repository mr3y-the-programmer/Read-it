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
import java.lang.IllegalArgumentException

class CustomIDHandlerTest {

    // Object under test
    private lateinit var customIDHandler: CustomIDHandler

    @Before
    fun setUp() {
        customIDHandler = CustomIDHandler()
    }

    @Test
    fun inputArticle2_ReturnValidId() {
        // When entering valid article
        val result = customIDHandler.getID(TestData.article2)

        val expected = "89479892-2pub-arti"

        // assert it matches our expectations
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun inputArticle1_ReturnValidId() {
        // When entering valid article
        val result = customIDHandler.getID(TestData.article1)

        val expected = "43259253-1pub-arti"

        // assert it matches our expectations
        assertThat(result).isEqualTo(expected)
    }

    @Test(expected = IllegalArgumentException::class)
    fun inputInvalidArticle_ThrowsException() {
        // When entering Invalid article
        val result = customIDHandler.getID(TestData.emptyArticle)

        // assert it throws an exception
    }

    @Test
    fun inputCategory1_ReturnValidId() {
        // When entering valid category
        val result = customIDHandler.getID(TestData.category1)

        val expected = "pPt-Programming-ego"

        // assert it matches our expectations
        assertThat(result).isEqualTo(expected)
    }

    @Test(expected = IllegalArgumentException::class)
    fun inputInvalidCategory_ThrowsException() {
        // When entering Invalid category
        val result = customIDHandler.getID(TestData.emptyCategory)

        // assert it throws an exception
    }
}
