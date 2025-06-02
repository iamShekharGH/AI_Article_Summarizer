package com.shekharhandigol.aiarticlesummarizer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.shekharhandigol.aiarticlesummarizer.util.DATABASE_NAME

@Database(entities = [Article::class, Summary::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun summaryDao(): SummaryDao
}

val MIGRATION_1_2 = object : Migration(2, 3) { // From old version 1 to new version 2
    override fun migrate(db: SupportSQLiteDatabase) {
        // Migration for Article table
        db.execSQL("ALTER TABLE $DATABASE_NAME ADD COLUMN tags TEXT NOT NULL DEFAULT ''") // TEXT because of TypeConverter
        db.execSQL("ALTER TABLE $DATABASE_NAME ADD COLUMN typeOfSummary TEXT NOT NULL DEFAULT 'SHORT'")
        db.execSQL("ALTER TABLE $DATABASE_NAME ADD COLUMN imageUrl TEXT NOT NULL DEFAULT ''")

        // Migration for Summary table
        db.execSQL("ALTER TABLE summaries ADD COLUMN ogText TEXT NOT NULL DEFAULT ''")
    }
}