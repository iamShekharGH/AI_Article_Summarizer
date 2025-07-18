package com.shekharhandigol.aiarticlesummarizer.core

data class GeminiJsoupResponse(
    val title: String,
    val toSummarise: String,
    val onSummarise: String,
    val imageUrl: String = "",
    val favouriteArticles: Boolean = false,
    val typeOfSummary: String = "",
    val articleUrl: String = "",
    val articleId: Int = -1,
    val summaryId: Int = -1,
    val tags: List<String> = emptyList()
)

data class GeminiJsoupResponseUiModel(
    val title: String,
    val toSummarise: String,
    val onSummarise: String,
    val imageUrl: String = "",
    val favouriteArticles: Boolean = false,
    val typeOfSummary: String = "",
    val articleUrl: String = "",
    val articleId: Int = -1,
    val summaryId: Int = -1,
    val tags: List<String> = emptyList()
)