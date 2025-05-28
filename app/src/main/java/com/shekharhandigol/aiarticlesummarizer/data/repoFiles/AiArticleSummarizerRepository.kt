package com.shekharhandigol.aiarticlesummarizer.data.repoFiles

import android.graphics.Bitmap
import com.shekharhandigol.aiarticlesummarizer.database.Article
import com.shekharhandigol.aiarticlesummarizer.database.ArticleWithSummaries
import com.shekharhandigol.aiarticlesummarizer.util.GeminiModelName
import com.shekharhandigol.aiarticlesummarizer.util.SummaryLength
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.graphics.createBitmap


@Singleton
class AiArticleSummarizerRepository @Inject constructor(
    private val remoteArticlesGeminiDataSource: RemoteArticlesGeminiDataSource,
    private val localStorageDataSource: LocalStorageDataSource,
    private val settingsDataSource: SettingsDataSource
) {
    fun summarizeArticleAndBitmap(
        bitmap: Bitmap = createBitmap(1, 1),
        url: String
    ): Flow<AiSummariserResult<String>> =
        remoteArticlesGeminiDataSource.summarizeArticleAndBitmap(bitmap, url)


    fun summarizeArticle(
        url: String
    ): Flow<AiSummariserResult<Pair<String, String>>> =
        remoteArticlesGeminiDataSource.summarizeArticle(url)

    fun summarizeArticleWithPrompt(
        prompt: String,
        text: String
    ): Flow<AiSummariserResult<Pair<String, String>>> =
        remoteArticlesGeminiDataSource.summarizeArticleWithPrompt(prompt = prompt, text = text)

    fun getAllArticles(): Flow<AiSummariserResult<List<Article>>> =
        localStorageDataSource.getAllArticles()

    fun getAllFavoriteArticles(): Flow<AiSummariserResult<List<Article>>> =
        localStorageDataSource.getAllFavoriteArticles()

    fun favouriteThisArticle(articleId: Int, currentFavouriteState: Boolean) {
        localStorageDataSource.favouriteThisArticle(articleId, currentFavouriteState)
    }

    fun getArticleWithSummaries(articleId: Int): Flow<AiSummariserResult<ArticleWithSummaries>> =
        localStorageDataSource.getArticleWithSummaries(articleId)

    fun insertArticleWithSummary(
        url: String, title: String, summary: String
    ): Flow<AiSummariserResult<Long>> =
        localStorageDataSource.insertArticleWithSummary(url, title, summary)


    fun searchArticles(query: String): Flow<AiSummariserResult<List<Article>>> =
        localStorageDataSource.searchArticles(query)

    suspend fun deleteArticleById(articleId: Int) =
        localStorageDataSource.deleteArticleById(articleId)

    suspend fun savePromptSettings(summaryLength: SummaryLength) {
        settingsDataSource.savePromptSettings(summaryLength)
    }

    fun getPromptSettings(): Flow<SummaryLength> = settingsDataSource.getPromptSettings()


    fun getDarkModeValue(): Flow<Boolean> = settingsDataSource.getDarkModeValue()

    suspend fun saveDarkModeValue(darkMode: Boolean) {
        settingsDataSource.saveDarkModeValue(darkMode)

    }

    suspend fun saveGeminiModel(modelName: GeminiModelName) {
        settingsDataSource.saveGeminiModel(modelName)
    }

    fun geminiModelNameFlow(): Flow<GeminiModelName> = settingsDataSource.geminiModelNameFlow()

}
