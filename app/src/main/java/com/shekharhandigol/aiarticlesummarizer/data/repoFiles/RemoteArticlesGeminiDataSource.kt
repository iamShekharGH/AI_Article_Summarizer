package com.shekharhandigol.aiarticlesummarizer.data.repoFiles


import android.graphics.Bitmap
import android.util.Log
import com.shekharhandigol.aiarticlesummarizer.data.GeminiApiService
import com.shekharhandigol.aiarticlesummarizer.data.SUMMARIZE_ARTICLE_PROMPT_LARGE
import com.shekharhandigol.aiarticlesummarizer.data.SUMMARIZE_ARTICLE_PROMPT_MEDIUM
import com.shekharhandigol.aiarticlesummarizer.data.SUMMARIZE_ARTICLE_PROMPT_SHORT
import com.shekharhandigol.aiarticlesummarizer.util.SummaryLength
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteArticlesGeminiDataSource @Inject constructor(
    private val geminiApiService: GeminiApiService,
    private val settingsDataSource: SettingsDataSource
) {
    fun summarizeArticleAndBitmap(
        bitmap: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888),
        url: String
    ): Flow<AiSummariserResult<String>> = flow {
        emit(AiSummariserResult.Loading)
        try {
            geminiApiService.sendPromptWithImage(bitmap, url)
            val summary = "This is a dummy summary of the article from $url."
            emit(AiSummariserResult.Success(summary))
        } catch (e: Exception) {
            emit(AiSummariserResult.Error(e))
        }
    }

    fun summarizeArticle(
        url: String
    ): Flow<AiSummariserResult<Pair<String, String>>> = flow {
        emit(AiSummariserResult.Loading)

        val promptSettings =
            settingsDataSource.getPromptSettings().firstOrNull() ?: SummaryLength.MEDIUM
        val prompt = when (promptSettings) {
            SummaryLength.SHORT -> SUMMARIZE_ARTICLE_PROMPT_SHORT
            SummaryLength.MEDIUM -> SUMMARIZE_ARTICLE_PROMPT_MEDIUM
            SummaryLength.LONG -> SUMMARIZE_ARTICLE_PROMPT_LARGE
        }
        try {
            val (title, articleText) = returnTextToSummarize(url)
            val text = prompt + "\n" + articleText

            val summary = geminiApiService.sendPrompt(text)
            if (summary.isNullOrEmpty()) {
                emit(AiSummariserResult.Error(Exception("Could not generate summary.")))
            } else {
                emit(AiSummariserResult.Success(Pair(title, summary)))
            }

        } catch (e: Exception) {
            emit(AiSummariserResult.Error(e))
        }
    }

    private suspend fun returnTextToSummarize(url: String): Pair<String, String> {
        return withContext(Dispatchers.IO) {
            try {
                val document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36")
                    .timeout(10000)
                    .get()

                val title = document.select("head > title").text()
                Log.i("Title:", "Title: $title")

                val articleText = extractArticleText(document).also {
                    Log.i(
                        "Article Text:",
                        "Article Text: $it"
                    )
                }
                Log.i("Article Text:", "Article Text: $articleText")

                Pair(title, articleText)

            } catch (e: IOException) {
                e.printStackTrace()
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }

    private fun extractArticleText(document: Document): String {
        return document.select("article").text().ifBlank {
            document.select(".article-body, #main-content, .post-content").text().ifBlank {
                document.select("body p").text()
            }
        }
    }


}