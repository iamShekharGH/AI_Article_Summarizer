package com.shekharhandigol.aiarticlesummarizer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface SummaryDao {

    @Query("SELECT * FROM summaries WHERE articleId = :articleId")
    suspend fun getSummariesForArticle(articleId: Int): List<Summary>

    @Insert
    suspend fun insertSummary(summary: Summary): Long

}