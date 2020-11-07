/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.custom_lint

import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.Test

class ProhibitedDependenciesDetectorTest {

    @Test
    fun `app depend On core should trigger lint rule`() { // No pun is intended :)
        lint().files(APP_GRADLE_FILE_VIOLATE, MOCK_PROJECT_FUN)
            .allowMissingSdk()
            .issues(ProhibitedDependenciesDetector.issue)
            .run()
            .expect("""
                You've depended on low-level module, Use high-level modules like :domain instead. [IncorrectDependencyGraph]
            """.trimIndent())
    }

    @Test
    fun `app depend On domain No problems`() {
        lint().files(APP_GRADLE_FILE)
            .allowMissingSdk()
            .issues(ProhibitedDependenciesDetector.issue)
            .run()
            .expectClean()
    }
}