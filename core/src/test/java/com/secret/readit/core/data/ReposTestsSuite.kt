/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.data

import com.secret.readit.core.data.articles.ArticlesRepositoryTest
import com.secret.readit.core.data.auth.AuthRepositoryTest
import com.secret.readit.core.data.categories.CategoryRepositoryTest
import com.secret.readit.core.data.publisher.PublisherRepositoryTest
import com.secret.readit.core.data.shared.StorageRepositoryTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * A Suite that runs all repositories tests to check quickly if there's any failure
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
    ArticlesRepositoryTest::class,
    AuthRepositoryTest::class,
    CategoryRepositoryTest::class,
    PublisherRepositoryTest::class,
    StorageRepositoryTest::class)
class ReposTestsSuite