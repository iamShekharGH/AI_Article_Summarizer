package com.shekharhandigol.aiarticlesummarizer.core

data class GeminiJsoupResponse(
    val title: String,
    val toSummarise: String,
    val onSummarise: String,
    val imageUrl: String = ""
)

data class GeminiJsoupResponseUiModel(
    val title: String,
    val toSummarise: String,
    val onSummarise: String,
    val imageUrl: String = ""
)