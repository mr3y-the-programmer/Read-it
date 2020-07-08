/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data.auth

import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.FirebaseFirestore
import com.nhaarman.mockitokotlin2.mock
import com.secret.readit.core.MainCoroutineRule
import com.secret.readit.core.result.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AuthRepositoryTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    // Object under test
    private lateinit var authRepository: AuthRepository

    private lateinit var mockedFirestore: FirebaseFirestore

    @Before
    fun setUp() {
        mockedFirestore = mock<FirebaseFirestore> {
            // no-op
        }

        authRepository = AuthRepository(FakeAuthDataSource(), mockedFirestore, mainCoroutineRule.testDispatcher)
    }

    @Test
    fun nullUser_ReturnsError() = mainCoroutineRule.runBlockingTest {
        // When we need to create a doc
        val result = authRepository.createDocIfPossible()

        // assert it throws an Error
        assertThat(result).isInstanceOf(Result.Error::class.java)
    }
}
