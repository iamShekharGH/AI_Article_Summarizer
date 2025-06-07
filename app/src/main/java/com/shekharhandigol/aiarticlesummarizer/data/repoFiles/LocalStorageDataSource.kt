package com.shekharhandigol.aiarticlesummarizer.data.repoFiles

import com.shekharhandigol.aiarticlesummarizer.core.AiSummariserResult
import com.shekharhandigol.aiarticlesummarizer.core.ArticleUiModel
import com.shekharhandigol.aiarticlesummarizer.core.ArticleWithSummaryUiModel
import com.shekharhandigol.aiarticlesummarizer.data.mappers.toArticleUiModel
import com.shekharhandigol.aiarticlesummarizer.data.mappers.toArticleWithSummaryUiModel
import com.shekharhandigol.aiarticlesummarizer.data.mappers.toDbArticle
import com.shekharhandigol.aiarticlesummarizer.data.mappers.toDbSummary
import com.shekharhandigol.aiarticlesummarizer.database.Article
import com.shekharhandigol.aiarticlesummarizer.database.ArticleDao
import com.shekharhandigol.aiarticlesummarizer.database.Summary
import com.shekharhandigol.aiarticlesummarizer.database.SummaryDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalStorageDataSource @Inject constructor(
    private val articleDao: ArticleDao,
    private val summaryDao: SummaryDao
) {
    fun getAllArticles(): Flow<AiSummariserResult<List<ArticleUiModel>>> = flow {
        try {
            val articles = articleDao.getAllArticles().map { it.toArticleUiModel() }
            emit(AiSummariserResult.Success(articles))
        } catch (e: Exception) {
            emit(AiSummariserResult.Error(e))
        }
    }.onStart { emit(AiSummariserResult.Loading) }

    fun getAllFavoriteArticles(): Flow<AiSummariserResult<List<ArticleUiModel>>> = flow {
        try {
            val articles = articleDao.getAllFavoriteArticles()
            emit(AiSummariserResult.Success(articles.map { it.toArticleUiModel() }))
        } catch (e: Exception) {
            emit(AiSummariserResult.Error(e))
        }
    }.onStart { emit(AiSummariserResult.Loading) }

    fun favouriteThisArticle(articleId: Int, currentFavouriteState: Boolean) {
        if (currentFavouriteState) {
            articleDao.favouriteThisArticle(articleId)
        } else {
            articleDao.removeFavouriteFromThisArticle(articleId)
        }
    }

    fun getArticleWithSummaries(articleId: Int): Flow<AiSummariserResult<ArticleWithSummaryUiModel>> =
        flow {
            try {
                val articleWithSummary = articleDao.getArticleWithSummaries(articleId)
                if (articleWithSummary == null) {
                    emit(AiSummariserResult.Error(Exception("Article not found")))
                    return@flow
                }
                emit(AiSummariserResult.Success(articleWithSummary.toArticleWithSummaryUiModel()))
            } catch (e: Exception) {
                emit(AiSummariserResult.Error(e))
            }
        }.onStart { emit(AiSummariserResult.Loading) }

    fun insertArticleWithSummary(
        url: String, title: String, summary: String
    ): Flow<AiSummariserResult<Long>> = flow {
        emit(AiSummariserResult.Loading)
        try {
            val articleId = articleDao.insertArticle(
                Article(
                    title = title,
                    articleUrl = url,
                    typeOfSummary = "",
                    imageUrl = ""
                )
            )
            summaryDao.insertSummary(
                Summary(
                    articleId = articleId.toInt(),
                    summaryText = summary,
                    ogText = ""
                )
            )
            emit(AiSummariserResult.Success((articleId)))
        } catch (e: Exception) {
            emit(AiSummariserResult.Error(e))
        }
    }

    fun insertArticleWithSummary(
        data: ArticleWithSummaryUiModel
    ): Flow<AiSummariserResult<Long>> = flow {
        try {
            val articleId = articleDao.insertArticle(data.articleUiModel.toDbArticle())
            summaryDao.insertSummaries(data.summaryUiModel.map { it.toDbSummary(articleId.toInt()) })

            emit(AiSummariserResult.Success((articleId)))
        } catch (e: Exception) {
            emit(AiSummariserResult.Error(e))
        }
    }.onStart { emit(AiSummariserResult.Loading) }


    fun searchArticles(query: String): Flow<AiSummariserResult<List<ArticleUiModel>>> = flow {
        emit(AiSummariserResult.Loading)
        try {
            val articles = articleDao.searchArticles(query).map { it.toArticleUiModel() }
            emit(AiSummariserResult.Success(articles))
        } catch (e: Exception) {
            emit(AiSummariserResult.Error(e))
        }
    }


    suspend fun deleteArticleById(articleId: Int) = articleDao.deleteArticleById(articleId)

}