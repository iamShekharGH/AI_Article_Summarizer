package com.shekharhandigol.aiarticlesummarizer.data.mappers

import com.shekharhandigol.aiarticlesummarizer.core.ArticleUiModel
import com.shekharhandigol.aiarticlesummarizer.core.ArticleWithSummaryUiModel
import com.shekharhandigol.aiarticlesummarizer.core.GeminiJsoupResponse
import com.shekharhandigol.aiarticlesummarizer.core.GeminiJsoupResponseUiModel
import com.shekharhandigol.aiarticlesummarizer.core.SummaryUiModel
import com.shekharhandigol.aiarticlesummarizer.database.ArticleWithSummaries
import com.shekharhandigol.aiarticlesummarizer.database.Article as DbArticle // Alias to avoid name clash
import com.shekharhandigol.aiarticlesummarizer.database.Summary as DbSummary // Alias to avoid name clash


fun DbArticle.toArticleUiModel(): ArticleUiModel {
    return ArticleUiModel(
        articleId = this.articleId,
        title = this.title,
        articleUrl = this.articleUrl,
        favouriteArticles = this.favouriteArticles,
        date = this.date,
        tags = this.tags, // Assuming direct mapping is fine
        typeOfSummary = this.typeOfSummary,
        imageUrl = this.imageUrl
    )
}

fun DbSummary.toSummaryUiModel(): SummaryUiModel {
    return SummaryUiModel(
        articleId = this.articleId,
        summaryText = this.summaryText,
        ogText = this.ogText
    )
}

fun List<DbSummary>.toSummaryUiModelList(): List<SummaryUiModel> {
    return this.map { it.toSummaryUiModel() }
}

fun mapToArticleWithSummaryUiModel(
    dbArticle: DbArticle,
    dbSummaries: List<DbSummary>
): ArticleWithSummaryUiModel {
    return ArticleWithSummaryUiModel(
        articleUiModel = dbArticle.toArticleUiModel(),
        summaryUiModel = dbSummaries.toSummaryUiModelList()
    )
}

fun ArticleWithSummaries.toArticleWithSummaryUiModel(): ArticleWithSummaryUiModel {
    return ArticleWithSummaryUiModel(
        articleUiModel = this.article.toArticleUiModel(),
        summaryUiModel = this.summaries.toSummaryUiModelList()
    )
}

fun ArticleUiModel.toDbArticle(articleId: Int = 0): DbArticle {
    return DbArticle(
        articleId = articleId,
        title = this.title,
        articleUrl = this.articleUrl,
        favouriteArticles = this.favouriteArticles,
        date = this.date,
        tags = this.tags,
        typeOfSummary = this.typeOfSummary,
        imageUrl = this.imageUrl
    )
}

fun SummaryUiModel.toDbSummary(articleId: Int, summaryId: Int = 0): DbSummary {
    return DbSummary(
        summaryId = summaryId,
        articleId = articleId,
        summaryText = this.summaryText,
        ogText = this.ogText
    )
}


fun GeminiJsoupResponse.toUiModel(): GeminiJsoupResponseUiModel = GeminiJsoupResponseUiModel(
    title = title,
    toSummarise = toSummarise,
    onSummarise = onSummarise,
    imageUrl = imageUrl,
    favouriteArticles = favouriteArticles,
    typeOfSummary = typeOfSummary,
    articleUrl = articleUrl,
    articleId = articleId,
    summaryId = summaryId,
    tags = tags
)

fun GeminiJsoupResponseUiModel.toArticleWithSummaryUiModel(): ArticleWithSummaryUiModel {
    val articleUiModel = ArticleUiModel(
        title = this.title,
        articleUrl = this.articleUrl,
        favouriteArticles = this.favouriteArticles,
        imageUrl = this.imageUrl,
        typeOfSummary = this.typeOfSummary,
        articleId = this.articleId,
        tags = this.tags,
    )
    val summaryUiModel = SummaryUiModel(
        summaryText = this.onSummarise,
        ogText = this.toSummarise,
        articleId = this.articleId
    )
    return ArticleWithSummaryUiModel(
        articleUiModel = articleUiModel,
        summaryUiModel = listOf(summaryUiModel)
    )
}