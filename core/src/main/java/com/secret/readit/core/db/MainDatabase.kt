/*
 *  Copyright (C) MR3Y - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by MR3Y <abdonasr379@gmail.com>, 2020.
 */

package com.secret.readit.core.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Room database which used only For drafts at least for now, may contain other tables in the future
 */
@Database(entities = [DraftArticle::class], version = 2, exportSchema = true)
@TypeConverters(TypeConverterUtils::class)
abstract class MainDatabase: RoomDatabase() {
    abstract fun draftsDAO(): DraftsDAO

    companion object {
        @Volatile
        private var INSTANCE: MainDatabase? = null

        fun getInstance(applicationContext: Context): MainDatabase{
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(applicationContext).also { INSTANCE = it}
            }
        }

        private fun buildDatabase(context: Context): MainDatabase {
            return Room.databaseBuilder(context, MainDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration() //For now, we haven't created any data so destructiveMigration would be great
                .build()
        }

        const val DATABASE_NAME = "read_it"
    }
}