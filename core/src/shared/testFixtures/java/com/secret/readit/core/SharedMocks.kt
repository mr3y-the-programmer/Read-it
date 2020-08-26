/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.secret.readit.core.data.categories.CategoryRepository
import com.secret.readit.core.data.publisher.PublisherRepository
import kotlinx.coroutines.test.runBlockingTest

class SharedMocks(private val rule: MainCoroutineRule) {

    val mockedPubRepo = mock<PublisherRepository> {
        rule.runBlockingTest {
            on(it.getPublisherInfo(TestData.publisher1.id)).doReturn(TestData.uiPublisher1)
            on(it.getPublisherInfo(TestData.publisher2.id)).thenReturn(TestData.uiPublisher2)
        }
    }
    val mockedCategoryRepo = mock<CategoryRepository> {
        rule.runBlockingTest {
            on(it.getCategories(TestData.articles1[0].categoryIds)).doReturn(TestData.categories)
            on(it.getCategories(TestData.article2.categoryIds)).thenReturn(listOf(TestData.category3))
        }
    }
}
