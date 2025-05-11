package com.shekharhandigol.aiarticlesummarizer.data

import android.graphics.Bitmap
import android.util.Log
import com.shekharhandigol.aiarticlesummarizer.database.Article
import com.shekharhandigol.aiarticlesummarizer.database.ArticleDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AiArticleSummarizerRepository @Inject constructor(
    private val articleDao: ArticleDao,
    private val geminiApiService: GeminiApiService
) {

    fun summarizeArticleAndBitmap(
        bitmap: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888),
        url: String
    ): Flow<Result<String>> = flow {
        emit(Result.Loading)
        try {
            geminiApiService.sendPromptWithImage(bitmap, url)
            val summary = "This is a dummy summary of the article from $url."
            emit(Result.Success("", summary))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    fun summarizeArticle(
        url: String
    ): Flow<Result<String>> = flow {
        emit(Result.Loading)
        try {
            val (title, articleText) = returnTextToSummarize(url)
            val text = SUMMARIZE_ARTICLE_PROMPT_SHORT + "\n" + articleText

            emit(Result.PartialSuccess(title, articleText))
            val summary = geminiApiService.sendPrompt(text)
            if (summary.isNullOrEmpty()) {
                emit(Result.Error(Exception("Could not generate summary.")))
            } else {
                emit(Result.Success(title, summary))
            }

        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    private suspend fun returnTextToSummarize(url: String): Pair<String, String> {
        return withContext(Dispatchers.IO) {
            try {
                val document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36")
                    .timeout(10000)
                    .get()
//                println("Document: $document")

                val title = document.select("head > title").text()
                Log.i("Title:", "Title: $title")

                val articleElements = document.select("article")
                val articleText = if (articleElements.isNotEmpty()) {
                    articleElements.text()
                } else {
                    val contentElements =
                        document.select(".article-body, #main-content, .post-content")
                    if (contentElements.isNotEmpty()) {
                        contentElements.text()
                    } else {
                        document.select("body p").text()
                    }
                }
                Log.i("Article Text:", "Article Text: $articleText")

                Pair(title, articleText)


            } catch (e: IOException) {
                e.printStackTrace()
                Pair("Error fetching URL: ${e.message}", "Error fetching URL: ${e.message}")
//                "Error fetching URL: ${e.message}"
            } catch (e: Exception) {
                e.printStackTrace()
                Pair("Error fetching URL: ", "${e.message}")
            }
        }
    }

    fun insertArticle(url: String, summary: String, title: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            articleDao.insertArticle(
                Article(
                    articleUrl = url,
                    title = title,
                    summaryText = summary
                )
            )
            emit(Result.Success(Unit, Unit))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    fun insertArticle(article: Article): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            articleDao.insertArticle(article)
            emit(Result.Success(Unit, Unit))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }


    fun getArticleById(articleId: Int): Flow<Result<Article?>> = flow {
        emit(Result.Loading)
        try {
            val article = articleDao.getArticleById(articleId)
            emit(Result.Success(article, article))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    fun getAllArticles(): Flow<Result<List<Article>>> = flow {
        emit(Result.Loading)
        try {
            val articles = articleDao.getAllArticles()
            emit(Result.Success(articles, articles))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }


}

sealed class Result<out T> {
    data class Success<out T>(val data1: T, val data2: T) : Result<T>()
    data class PartialSuccess<out T>(val data1: T, val data2: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}