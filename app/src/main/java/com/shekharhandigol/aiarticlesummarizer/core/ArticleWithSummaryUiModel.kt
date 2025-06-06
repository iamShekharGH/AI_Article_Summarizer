package com.shekharhandigol.aiarticlesummarizer.core

data class ArticleWithSummaryUiModel(
    val articleUiModel: ArticleUiModel,
    val summaryUiModel: List<SummaryUiModel>
)


data class ArticleUiModel(
    val articleId: Int = 0,
    val title: String,
    val articleUrl: String,
    val favouriteArticles: Boolean = false,
    val date: Long = System.currentTimeMillis(),

    val tags: List<String> = emptyList(),
    val typeOfSummary: String,
    val imageUrl: String = "https://images.pexels.com/photos/1925536/pexels-photo-1925536.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
)

data class SummaryUiModel(
    val articleId: Int,
    val summaryText: String,
    val ogText: String
)