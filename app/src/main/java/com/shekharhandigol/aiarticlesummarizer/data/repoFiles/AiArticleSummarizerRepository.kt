package com.shekharhandigol.aiarticlesummarizer.data.repoFiles

import android.graphics.Bitmap
import androidx.core.graphics.createBitmap
import com.shekharhandigol.aiarticlesummarizer.core.AiSummariserResult
import com.shekharhandigol.aiarticlesummarizer.core.ArticleUiModel
import com.shekharhandigol.aiarticlesummarizer.core.ArticleWithSummaryUiModel
import com.shekharhandigol.aiarticlesummarizer.core.GeminiJsoupResponseUiModel
import com.shekharhandigol.aiarticlesummarizer.util.AppThemeOption
import com.shekharhandigol.aiarticlesummarizer.util.GeminiModelName
import com.shekharhandigol.aiarticlesummarizer.util.SummaryLength
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


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
    ): Flow<AiSummariserResult<GeminiJsoupResponseUiModel>> =
        remoteArticlesGeminiDataSource.summarizeArticle(url)

    fun summarizeArticleWithPrompt(
        prompt: String,
        text: String
    ): Flow<AiSummariserResult<Pair<String, String>>> =
        remoteArticlesGeminiDataSource.summarizeArticleWithPrompt(prompt = prompt, text = text)

    fun getAllArticles(): Flow<AiSummariserResult<List<ArticleUiModel>>> =
        localStorageDataSource.getAllArticles()

    fun getAllFavoriteArticles(): Flow<AiSummariserResult<List<ArticleUiModel>>> =
        localStorageDataSource.getAllFavoriteArticles()

    fun favouriteThisArticle(articleId: Int, currentFavouriteState: Boolean) {
        localStorageDataSource.favouriteThisArticle(articleId, currentFavouriteState)
    }

    fun getArticleWithSummaries(articleId: Int): Flow<AiSummariserResult<ArticleWithSummaryUiModel>> =
        localStorageDataSource.getArticleWithSummaries(articleId)

    fun insertArticleWithSummary(
        articleWithSummaryUiModel: ArticleWithSummaryUiModel
    ): Flow<AiSummariserResult<Long>> =
        localStorageDataSource.insertArticleWithSummary(articleWithSummaryUiModel)


    fun searchArticles(query: String): Flow<AiSummariserResult<List<ArticleUiModel>>> =
        localStorageDataSource.searchArticles(query)

    suspend fun deleteArticleById(articleId: Int) =
        localStorageDataSource.deleteArticleById(articleId)

    suspend fun savePromptSettings(summaryLength: SummaryLength) {
        settingsDataSource.savePromptSettings(summaryLength)
    }

    fun getPromptSettings(): Flow<SummaryLength> = settingsDataSource.getPromptSettings()


    suspend fun saveGeminiModel(modelName: GeminiModelName) {
        settingsDataSource.saveGeminiModel(modelName)
    }

    fun geminiModelNameFlow(): Flow<GeminiModelName> = settingsDataSource.geminiModelNameFlow()

    suspend fun saveThemeName(themeName: AppThemeOption) {
        settingsDataSource.saveThemeName(themeName)
    }

    fun getThemeName(): Flow<AppThemeOption> = settingsDataSource.getThemeName()

}
