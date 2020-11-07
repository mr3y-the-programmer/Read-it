/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.custom_lint

import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.google.common.annotations.Beta
import org.junit.Test

/**
 * Test for [PrivateConstructorDetector]
 */
@Beta
class PrivateConstructorDetectorTest {

    @Test
    fun `Calling text Element copy() with imgUri should trigger lint rule`() {
        lint().files(ARBITRARY_KT_CLASS_VIOLATE)
            .allowMissingSdk() //allow the sdk to be missing or set it with sdkHome()
            .issues(PrivateConstructorDetector.issue)
            .run()
            .expect("""
                You've accessed Private constructor params. [ProhibitedCopyCall]
            """.trimIndent())
    }

    @Test
    fun `Calling text Element with valid copy() No problems`() {
        lint().files(ARBITRARY_KT_CLASS)
            .allowMissingSdk()
            .issues(PrivateConstructorDetector.issue)
            .run()
            .expectClean()
    }
}