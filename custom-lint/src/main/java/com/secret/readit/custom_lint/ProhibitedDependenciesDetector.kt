/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.custom_lint

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.GradleScanner
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.intellij.psi.PsiMethod
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import org.jetbrains.uast.UCallExpression

/**
 * Detector to prevent usage of some dependencies in incorrect modules, like: ":app" depend on -> ":core"
 */
@Suppress("UnstableApiUsage")
class ProhibitedDependenciesDetector: Detector(), GradleScanner {

    override fun getApplicableMethodNames(): List<String>? = listOf(PROJECT_FUN)

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (verifyViolation(context, method)){
            context.report(issue, context.getLocation(node), BRIEF_DESCRIPTION, createLintFix(method, fixes[0]))
        }
    }

    private fun verifyViolation(context: JavaContext, method: PsiMethod): Boolean {
        val clientModuleName = context.project.name
        val libraryModuleName = method.parameterList.parameters[0].safeAs<String>() ?: return false //Guard against project(map<>)
        prohibitedRelations.entries.forEach { entry ->
            if (entry.key == clientModuleName && entry.value == libraryModuleName) return true
        }
        return false
    }

    private fun createLintFix(oldDependencyMethod: PsiMethod, newDependency: String): LintFix{
        return LintFix.create()
            .name("Replace core with domain dependency")
            .replace()
            .text(oldDependencyMethod.parameterList.parameters[0].safeAs())
            .with(newDependency)
            .robot(true)
            .independent(true)
            .build()
    }

    companion object {
        const val PROJECT_FUN = "project"
        val prohibitedRelations = mapOf( // Contains prohibited dependency relations
            ":app" to ":core"
        )
        val fixes = listOf(":domain")
        const val BRIEF_DESCRIPTION = "You've depended on low-level module, Use high-level modules like :domain instead"
        val issue = Issue.create(
            id = "IncorrectDependencyGraph",
            briefDescription = BRIEF_DESCRIPTION,
            explanation = "To provide a better and clear abstraction and keep architecture clean, some Ui modules like :app can't" +
                "depend directly on low-level modules like :core, this'll also keep build time more faster because depending on arbitrary modules" +
                "can significantly slow down build.",
            category = Category.PERFORMANCE,
            priority = 8,
            severity = Severity.ERROR,
            implementation = Implementation(
                ProhibitedDependenciesDetector::class.java,
                Scope.GRADLE_SCOPE
            )
        )
    }
}