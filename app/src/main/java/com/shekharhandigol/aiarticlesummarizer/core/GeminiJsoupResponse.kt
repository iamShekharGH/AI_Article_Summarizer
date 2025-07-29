package com.shekharhandigol.aiarticlesummarizer.core

data class GeminiJsoupResponse(
    val title: String,
    val toSummarise: String,
    val onSummarise: String,
    val imageUrl: String = "",
    val favouriteArticles: Boolean = false,
    val articleUrl: String = "",
    val articleId: Int = -1,
    val summaryId: Int = -1,
    val tags: List<String> = emptyList(),
    val summaryType: SummaryType = SummaryType.MEDIUM_SUMMARY
)

data class GeminiJsoupResponseUiModel(
    val title: String,
    val toSummarise: String,
    val onSummarise: String,
    val imageUrl: String = "",
    val favouriteArticles: Boolean = false,
    val articleUrl: String = "",
    val articleId: Int = -1,
    val summaryId: Int = -1,
    val tags: List<String> = emptyList(),
    val summaryType: SummaryType
)