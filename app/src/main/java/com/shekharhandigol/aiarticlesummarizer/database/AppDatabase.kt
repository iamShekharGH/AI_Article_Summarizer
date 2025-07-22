package com.shekharhandigol.aiarticlesummarizer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.shekharhandigol.aiarticlesummarizer.util.DATABASE_NAME

@Database(entities = [Article::class, Summary::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun summaryDao(): SummaryDao
}

val MIGRATION_2_3 = object : Migration(2, 3) { // From old version 1 to new version 2
    override fun migrate(db: SupportSQLiteDatabase) {
        // Migration for Article table
        db.execSQL("ALTER TABLE $DATABASE_NAME ADD COLUMN tags TEXT NOT NULL DEFAULT ''") // TEXT because of TypeConverter
        db.execSQL("ALTER TABLE $DATABASE_NAME ADD COLUMN typeOfSummary TEXT NOT NULL DEFAULT 'NOT_SET'")
        db.execSQL("ALTER TABLE $DATABASE_NAME ADD COLUMN imageUrl TEXT NOT NULL DEFAULT ''")

        // Migration for Summary table
        db.execSQL("ALTER TABLE summaries ADD COLUMN ogText TEXT NOT NULL DEFAULT ''")
    }
}

// NEW MIGRATION FROM VERSION 3 TO 4
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {

        db.execSQL("ALTER TABLE summaries ADD COLUMN summaryType TEXT NOT NULL DEFAULT 'MEDIUM_SUMMARY'")

        db.execSQL(
            """
            UPDATE summaries
            SET summaryType = (
                SELECT articles.typeOfSummary
                FROM $DATABASE_NAME AS articles
                WHERE articles.articleId = summaries.articleId
            )
            WHERE EXISTS (
                SELECT 1
                FROM $DATABASE_NAME AS articles
                WHERE articles.articleId = summaries.articleId
            )
        """
        )
        db.execSQL("UPDATE summaries SET summaryType = 'MEDIUM_SUMMARY' WHERE summaryType IS NULL OR summaryType = ''")


        // Create a new temporary Article table without 'typeOfSummary'
        db.execSQL(
            """
            CREATE TABLE new_$DATABASE_NAME (
                articleId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                articleUrl TEXT NOT NULL,
                favouriteArticles INTEGER NOT NULL DEFAULT 0,
                date INTEGER NOT NULL,
                tags TEXT NOT NULL DEFAULT '',
                imageUrl TEXT NOT NULL DEFAULT ''
            )
        """
        )

        // Copy data from the old Article table to the new one
        db.execSQL(
            """
            INSERT INTO new_$DATABASE_NAME (articleId, title, articleUrl, favouriteArticles, date, tags, imageUrl)
            SELECT articleId, title, articleUrl, favouriteArticles, date, tags, imageUrl
            FROM $DATABASE_NAME
        """
        )

        // Drop the old Article table
        db.execSQL("DROP TABLE $DATABASE_NAME")

        // Rename the new table to the original table name
        db.execSQL("ALTER TABLE new_$DATABASE_NAME RENAME TO $DATABASE_NAME")

    }
}