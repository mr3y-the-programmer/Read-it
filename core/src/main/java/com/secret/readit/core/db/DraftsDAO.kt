/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.db

import androidx.room.*
import com.secret.readit.model.articleId
import kotlinx.coroutines.flow.Flow

@Dao
interface DraftsDAO {

    @Query("SELECT title, num_minutes_read, time_in_days FROM articles ORDER BY time_in_days ASC")
    suspend fun getDrafts(): Flow<List<DraftArticle>>

    @Query("SELECT * FROM articles WHERE id = :id")
    suspend fun getDraft(id: articleId): DraftArticle

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDraft(article: DraftArticle)

    @Query("DELETE FROM articles WHERE time_in_days > 30")
    suspend fun deleteDrafts()
}