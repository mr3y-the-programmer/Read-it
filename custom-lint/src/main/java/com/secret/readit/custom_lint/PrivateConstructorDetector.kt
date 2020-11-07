/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.custom_lint

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiParameter
import com.secret.readit.model.Element
import org.jetbrains.uast.UCallExpression

/**
 * Detector of accessing primary constructor of [Element] through copy() fun which isn't allowed
 */
@Suppress("UnstableApiUsage")
class PrivateConstructorDetector: Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames(): List<String>? = listOf(COPY_FUN)

    //TODO: consider if it is worth migrating to afterCheckFile approach
    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        // context.evaluator.computeArgumentMapping(node, method)
        if (checkViolation(node, method)) context.report(issue, context.getLocation(node), ISSUE_DESCRIPTION)
    }

    /**
     * @return true if there's a violation occurred otherwise false
     */
    private fun checkViolation(node: UCallExpression, method: PsiMethod): Boolean {
        val type = node.receiverType
        if (type is PsiClassType && type.className == ELEMENT) {
            val elementParams = type.resolve()?.fields!!
            val copyParams = method.parameterList.parameters
            return checkDiff(elementParams, copyParams)
        }
        return false // No violation
    }

    private fun checkDiff(params1: Array<PsiField>, params2: Array<PsiParameter>): Boolean{
        repeat(params1.size) { psiFieldIndex ->
            run {
                 repeat(params2.size) { psiParameterIndex ->
                     if (params1[psiFieldIndex].name == params2[psiParameterIndex].name) return@run
                 }
             }
        }
        return false
    }

    companion object {
        const val ELEMENT = "Element"
        const val COPY_FUN = "copy"
        const val ISSUE_DESCRIPTION = "You've accessed Private constructor properties"
        val issue = Issue.create(
            id = "ProhibitedCopyCall",
            briefDescription = ISSUE_DESCRIPTION,
            explanation = "You've accessed Private Element constructor params, This could lead to subtle, unexpected and a really hard to catch bugs." +
                "Please make sure you've called copy() with the correct params, check if you've mixed imageUrl argument with text argument ",
            category = Category.CORRECTNESS,
            priority = 9, // takes a number from 1(low important) to 10(critical/severe)
            severity = Severity.FATAL,
            implementation = Implementation(
                PrivateConstructorDetector::class.java,
                Scope.ALL_CLASSES_AND_LIBRARIES
            )
        )
    }
}