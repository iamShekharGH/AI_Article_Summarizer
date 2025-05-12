package com.shekharhandigol.aiarticlesummarizer.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Article::class, Summary::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun summaryDao(): SummaryDao
}