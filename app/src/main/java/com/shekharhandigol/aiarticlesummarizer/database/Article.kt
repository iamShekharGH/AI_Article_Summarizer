package com.shekharhandigol.aiarticlesummarizer.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shekharhandigol.aiarticlesummarizer.util.DATABASE_NAME

@Entity(tableName = DATABASE_NAME)
data class Article(
    @PrimaryKey(autoGenerate = true)
    val articleId: Int = 0,
    val title: String,
    val articleUrl: String,
    val date: Long = System.currentTimeMillis(),
    val summaryText: String
)

/*
@Entity(tableName = "summaries")
data class Summary(
    @PrimaryKey(autoGenerate = true)
    val summaryId: Int = 0,
    val articleId: Int,
    val summaryText: String
)*/
