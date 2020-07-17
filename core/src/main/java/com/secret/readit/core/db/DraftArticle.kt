/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.secret.readit.model.BaseElement
import com.secret.readit.model.articleId

/**
 * Article Entity saved in database which is used for drafts feature
 */
@Entity(tableName = "articles")
data class DraftArticle(@PrimaryKey val id: articleId,
                        val title: String,
                        val elements: List<BaseElement>,
                        @ColumnInfo(name = "num_minutes_read") val numMinutesRead: Int,
                        @ColumnInfo(name = "categories_ids") val categoryIds: List<String>,
                        @ColumnInfo(name = "time_in_days") val timeInDays: Int)
//TODO(Optional): convert timeInDays Type to OffsetDateTime