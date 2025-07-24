package com.shekharhandigol.aiarticlesummarizer.core

import com.shekharhandigol.aiarticlesummarizer.database.Article
import com.shekharhandigol.aiarticlesummarizer.database.Summary
import kotlinx.serialization.Serializable

@Serializable
data class AppBackupData(
    val articles: List<Article>,
    val summaries: List<Summary>
)