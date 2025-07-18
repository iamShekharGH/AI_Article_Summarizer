package com.shekharhandigol.aiarticlesummarizer.data.repoFiles


import android.graphics.Bitmap
import android.util.Log
import androidx.core.graphics.createBitmap
import com.shekharhandigol.aiarticlesummarizer.core.AiSummariserResult
import com.shekharhandigol.aiarticlesummarizer.core.GeminiJsoupResponse
import com.shekharhandigol.aiarticlesummarizer.core.GeminiJsoupResponseUiModel
import com.shekharhandigol.aiarticlesummarizer.core.SummaryType
import com.shekharhandigol.aiarticlesummarizer.core.TAG_GENERATION_PROMPT
import com.shekharhandigol.aiarticlesummarizer.data.GeminiApiService
import com.shekharhandigol.aiarticlesummarizer.data.mappers.toUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
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
        bitmap: Bitmap = createBitmap(1, 1),
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
        url: String,
        summaryType: SummaryType? = null
    ): Flow<AiSummariserResult<GeminiJsoupResponseUiModel>> = flow {

        val promptSettings = summaryType ?: settingsDataSource.getPromptSettings().firstOrNull()
        ?: SummaryType.MEDIUM_SUMMARY

        val prompt = promptSettings.prompt
        val articleSummary = returnTextToSummarize(url)
        val text = prompt + "\n" + articleSummary.toSummarise

        val summary = geminiApiService.sendPrompt(text)
        val tags = generateTags(articleSummary.toSummarise)


        if (summary.isNullOrEmpty()) {
            emit(AiSummariserResult.Error(Exception("Could not generate summary.")))
        } else {
            emit(
                AiSummariserResult.Success(
                    articleSummary.copy(
                        onSummarise = summary,
                        articleUrl = url,
                        tags = tags,
                        summaryType = promptSettings
                    ).toUiModel()
                )
            )
        }

    }.onStart { emit(AiSummariserResult.Loading) }
        .catch { e: Throwable ->
            e.printStackTrace()
            emit(AiSummariserResult.Error(e))
        }

    fun summarizeArticleWithPrompt(
        prompt: String,
        text: String
    ): Flow<AiSummariserResult<Pair<String, String>>> = flow {

        val summary = geminiApiService.sendPrompt(text)
        if (summary.isNullOrEmpty()) {
            emit(AiSummariserResult.Error(Exception("Could not generate summary.")))
        } else {
            emit(AiSummariserResult.Success(Pair(prompt, summary)))
        }

    }.onStart { emit(AiSummariserResult.Loading) }
        .catch { e: Throwable ->
            e.printStackTrace()
            emit(AiSummariserResult.Error(e))
        }

    suspend fun generateTags(articleText: String): List<String> {
        val tagsPrompt = "$TAG_GENERATION_PROMPT\n$articleText"
        val rawTags = geminiApiService.sendPrompt(tagsPrompt)

        return if (rawTags == null ||
            rawTags.contains("Please provide the article content.", ignoreCase = true)
        ) {
            Log.w("TagsGeneration", "Error in generating tags or invalid format: $rawTags")
            emptyList()
        } else {
            rawTags.replace("*", "")
                .split(",")
                .flatMap { it.split("\n") }
                .map { it.trim() }
                .filter { it.isNotEmpty() }
        }
    }

    private suspend fun returnTextToSummarize(url: String): GeminiJsoupResponse {
        return withContext(Dispatchers.IO) {
            try {
                val document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36")
                    .timeout(10000)
                    .get()

                val title = document.select("head > title").text()
                Log.i("Title:", "Title: $title")

                val imageUrl = document.select("meta[property=og:image]").attr("content")
                    .takeIf { it.isNotEmpty() }
                Log.i("Image URL:", "Image URL: $imageUrl")

                val articleText = extractArticleText(document).also {
                    Log.i(
                        "Article Text:",
                        "Article Text: $it"
                    )
                }

                GeminiJsoupResponse(
                    title = title,
                    toSummarise = articleText,
                    imageUrl = imageUrl ?: "",
                    onSummarise = ""
                )

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