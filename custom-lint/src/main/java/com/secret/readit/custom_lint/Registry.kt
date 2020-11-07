/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.custom_lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import com.google.common.annotations.Beta

/**
 * this is like the entry point for android lint to discover our issues,
 *
 * it knows about registry by Using ServiceLocator pattern, see [resources] folder.
 *
 * NOTE: You can also let android lint know about this registry by adding jar {} in build.gradle
 */
@Beta
class Registry: IssueRegistry() {
    override val issues: List<Issue>
        get() = listOf(
            PrivateConstructorDetector.issue,
            ProhibitedDependenciesDetector.issue
        )

    override val api: Int
        get() = CURRENT_API //Current Lint's API version
}