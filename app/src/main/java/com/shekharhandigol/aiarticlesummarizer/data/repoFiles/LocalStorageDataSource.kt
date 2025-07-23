package com.shekharhandigol.aiarticlesummarizer.data.repoFiles

import android.util.Log
import com.shekharhandigol.aiarticlesummarizer.core.AiSummariserResult
import com.shekharhandigol.aiarticlesummarizer.core.ArticleUiModel
import com.shekharhandigol.aiarticlesummarizer.core.ArticleWithSummaryUiModel
import com.shekharhandigol.aiarticlesummarizer.core.SummaryUiModel
import com.shekharhandigol.aiarticlesummarizer.data.mappers.toArticleUiModel
import com.shekharhandigol.aiarticlesummarizer.data.mappers.toArticleWithSummaryUiModel
import com.shekharhandigol.aiarticlesummarizer.data.mappers.toDbArticle
import com.shekharhandigol.aiarticlesummarizer.data.mappers.toDbSummary
import com.shekharhandigol.aiarticlesummarizer.database.ArticleDao
import com.shekharhandigol.aiarticlesummarizer.database.SummaryDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalStorageDataSource @Inject constructor(
    private val articleDao: ArticleDao,
    private val summaryDao: SummaryDao
) {
    fun getAllArticles(): Flow<AiSummariserResult<List<ArticleUiModel>>> =
        articleDao.getAllArticles()
            .map { list -> list.map { it.toArticleUiModel() } }
            .map<List<ArticleUiModel>, AiSummariserResult<List<ArticleUiModel>>> {
                AiSummariserResult.Success(
                    it
                )
            }
            .onStart { emit(AiSummariserResult.Loading) }
            .catch { e -> emit(AiSummariserResult.Error(e)) }

    fun getAllFavoriteArticles(): Flow<AiSummariserResult<List<ArticleUiModel>>> =
        articleDao.getAllFavoriteArticles()
            .map { list -> list.map { it.toArticleUiModel() } }
            .map<List<ArticleUiModel>, AiSummariserResult<List<ArticleUiModel>>> {
                AiSummariserResult.Success(
                    it
                )
            }
            .onStart { emit(AiSummariserResult.Loading) }
            .catch { e -> emit(AiSummariserResult.Error(e)) }

    fun getAllTags(): Flow<AiSummariserResult<List<String>>> =
        articleDao.getAllTagsFlow()
            .map { tagsList ->
                tagsList.flatMap { it.split(",") }.map { it.trim().removeSurrounding("[", "]") }
                    .distinct().filter { it.isNotEmpty() }
            }
            .map<List<String>, AiSummariserResult<List<String>>> {
                AiSummariserResult.Success(
                    it
                )
            }
            .onStart { emit(AiSummariserResult.Loading) }
            .catch { e -> emit(AiSummariserResult.Error(e)) }

    fun getArticlesByTag(tag: String): Flow<AiSummariserResult<List<ArticleUiModel>>> =
        articleDao.getArticlesByTag(tag).map { list -> list.map { it.toArticleUiModel() } }
            .map<List<ArticleUiModel>, AiSummariserResult<List<ArticleUiModel>>> {
                AiSummariserResult.Success(
                    it
                )
            }
            .onStart { emit(AiSummariserResult.Loading) }
            .catch { e -> emit(AiSummariserResult.Error(e)) }


    fun favouriteThisArticle(articleId: Int, setThisFavouriteState: Boolean) {
        if (setThisFavouriteState) {
            articleDao.favouriteThisArticle(articleId)
        } else {
            articleDao.removeFavouriteFromThisArticle(articleId)
        }
    }

    fun getArticleWithSummaries(articleId: Int): Flow<AiSummariserResult<ArticleWithSummaryUiModel>> {
        return articleDao.getArticleWithSummaries(articleId)
            .map { articleWithSummaryDb ->
                if (articleWithSummaryDb != null) {
                    AiSummariserResult.Success(articleWithSummaryDb.toArticleWithSummaryUiModel())
                } else {
                    AiSummariserResult.Error(Exception("Article with ID $articleId not found"))
                }
            }
            .onStart { emit(AiSummariserResult.Loading) }
            .catch { e ->
                Log.e("LocalStorageDataSource", "Error: ${e.message}", e)
                emit(AiSummariserResult.Error(e))
            }
    }

    fun insertArticleWithSummary(
        data: ArticleWithSummaryUiModel
    ): Flow<AiSummariserResult<Long>> = flow {
        try {

            val articleId = articleDao.insertArticleAndSummaries(
                data.articleUiModel.toDbArticle(),
                data.summaryUiModel.map {
                    it.toDbSummary(
                        data.articleUiModel.articleId
                    )
                })

            emit(AiSummariserResult.Success((articleId)))
        } catch (e: Exception) {
            emit(AiSummariserResult.Error(e))
        }
    }.onStart { emit(AiSummariserResult.Loading) }
        .catch { e ->
            e.printStackTrace()
            Log.e("error", e.message.toString())
            emit(AiSummariserResult.Error(e))

        }

    fun insertSummary(summaryUiModel: SummaryUiModel): Flow<AiSummariserResult<Long>> {
        return flow {
            emit(AiSummariserResult.Loading)
            try {
                val summaryId =
                    summaryDao.insertSummary(summaryUiModel.toDbSummary(summaryUiModel.articleId))
                emit(AiSummariserResult.Success(summaryId))
            } catch (e: Exception) {
                emit(AiSummariserResult.Error(e))
            }
        }
    }

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