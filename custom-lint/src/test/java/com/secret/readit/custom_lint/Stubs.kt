/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.custom_lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.java
import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestFiles.gradle

/**
 * Fake Test files for sake of testing
 */

 val ARBITRARY_KT_CLASS_VIOLATE = kotlin( "com/example/Arbitrary.kt",
                """
                    package com.secret.readit.core

                    class Arbitrary {
    
                        fun modifyElement() {
                            val element = Element(
                                text = "This is text",
                                markup = Markup(MarkupType.TEXT, 0, 12))
        
                            val newElement = element.copy(imageUrl = "New imgUrl")
                        }
                    }
                """.trimIndent()
).indented().within("src") //Example of file within src directory

val ARBITRARY_KT_CLASS = kotlin( "com/example/Arbitrary.kt",
                """
                    package com.secret.readit.core

                    class Arbitrary {
    
                        fun modifyElement() {
                            val element = Element(
                                text = "This is text",
                                markup = Markup(MarkupType.TEXT, 0, 12))
        
                            val newElement = element.copy(text = "New Text")
                        }
                    }
                """.trimIndent()
).indented().within("src") //Example of file within src directory


val APP_GRADLE_FILE_VIOLATE = gradle("app/build.gradle",
                """
                    apply plugin: 'com.android.application'
                    
                    android {
                        defaultConfig {
                            applicationId "com.secret.readit"
                        }
                    }
                    
                    dependencies {
                        implementation project(":core")
                    }
                """.trimIndent()
).indented().within("Readit")

val APP_GRADLE_FILE = gradle("app/build.gradle",
                """
                    apply plugin: 'com.android.application'
                    
                    android {
                        defaultConfig {
                            applicationId "com.secret.readit"
                        }
                    }
                    
                    dependencies {
                        implementation project(":domain")
                    }
                """.trimIndent()
).indented().within("Readit")

val MOCK_PROJECT_FUN = java("org/gradle/api/Project.java",
                """
                   public interface Project {
                        Project project(String path);
                   }
                """.trimIndent()
).indented().within("src")
