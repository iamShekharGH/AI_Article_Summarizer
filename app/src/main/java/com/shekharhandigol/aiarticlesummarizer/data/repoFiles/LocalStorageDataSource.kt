package com.shekharhandigol.aiarticlesummarizer.data.repoFiles

import com.shekharhandigol.aiarticlesummarizer.database.Article
import com.shekharhandigol.aiarticlesummarizer.database.ArticleDao
import com.shekharhandigol.aiarticlesummarizer.database.ArticleWithSummaries
import com.shekharhandigol.aiarticlesummarizer.database.Summary
import com.shekharhandigol.aiarticlesummarizer.database.SummaryDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalStorageDataSource @Inject constructor(
    private val articleDao: ArticleDao,
    private val summaryDao: SummaryDao
) {
    fun getAllArticles(): Flow<AiSummariserResult<List<Article>>> = flow {
        emit(AiSummariserResult.Loading)
        try {
            val articles = articleDao.getAllArticles()
            emit(AiSummariserResult.Success(articles))
        } catch (e: Exception) {
            emit(AiSummariserResult.Error(e))
        }
    }

    fun getAllFavoriteArticles(): Flow<AiSummariserResult<List<Article>>> = flow {
        emit(AiSummariserResult.Loading)
        try {
            val articles = articleDao.getAllFavoriteArticles()
            emit(AiSummariserResult.Success(articles))
        } catch (e: Exception) {
            emit(AiSummariserResult.Error(e))
        }
    }

    fun favouriteThisArticle(articleId: Int, currentFavouriteState: Boolean) {
        if (currentFavouriteState) {
            articleDao.removeFavouriteFromThisArticle(articleId)
        } else {
            articleDao.favouriteThisArticle(articleId)
        }
    }

    fun getArticleWithSummaries(articleId: Int): Flow<AiSummariserResult<ArticleWithSummaries>> =
        flow {
            emit(AiSummariserResult.Loading)
            try {
                val articleWithSummary = articleDao.getArticleWithSummaries(articleId)
                if (articleWithSummary == null) {
                    emit(AiSummariserResult.Error(Exception("Article not found")))
                    return@flow
                }
                emit(AiSummariserResult.Success(articleWithSummary))
            } catch (e: Exception) {
                emit(AiSummariserResult.Error(e))
            }
        }

    fun insertArticleWithSummary(
        url: String, title: String, summary: String
    ): Flow<AiSummariserResult<Long>> = flow {
        emit(AiSummariserResult.Loading)
        try {
            val articleId = articleDao.insertArticle(
                Article(
                    title = title,
                    articleUrl = url
                )
            )
            summaryDao.insertSummary(
                Summary(
                    articleId = articleId.toInt(),
                    summaryText = summary
                )
            )
            emit(AiSummariserResult.Success((articleId)))
        } catch (e: Exception) {
            emit(AiSummariserResult.Error(e))
        }
    }


    fun searchArticles(query: String): Flow<AiSummariserResult<List<Article>>> = flow {
        emit(AiSummariserResult.Loading)
        try {
            val articles = articleDao.searchArticles(query)
            emit(AiSummariserResult.Success(articles))
        } catch (e: Exception) {
            emit(AiSummariserResult.Error(e))
        }
    }


    suspend fun deleteArticleById(articleId: Int) = articleDao.deleteArticleById(articleId)

}