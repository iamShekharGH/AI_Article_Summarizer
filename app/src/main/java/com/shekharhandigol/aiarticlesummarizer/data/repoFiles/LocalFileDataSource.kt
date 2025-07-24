package com.shekharhandigol.aiarticlesummarizer.data.repoFiles

import android.content.Context
import android.net.Uri
import com.shekharhandigol.aiarticlesummarizer.core.AiSummariserResult
import com.shekharhandigol.aiarticlesummarizer.core.AppBackupData
import com.shekharhandigol.aiarticlesummarizer.database.ArticleDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalFileDataSource @Inject constructor(
    private val json: Json,
    private val context: Context,
    private val articleDao: ArticleDao,
) {

    fun exportDataToFile(uri: Uri): Flow<AiSummariserResult<String>> = flow {
        emit(AiSummariserResult.Loading)
        try {
            val articles = articleDao.getAllArticlesList()
            val summaries = articleDao.getAllSummaries()
            val backupData = AppBackupData(articles, summaries)
            val jsonString = json.encodeToString(backupData)
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(jsonString)
                }
            } ?: throw Exception("Failed to open output stream for URI: $uri")
            emit(AiSummariserResult.Success("Data exported successfully"))
        } catch (e: Exception) {
            emit(AiSummariserResult.Error(e))
        }
    }

    fun importDataFromFile(uri: Uri): Flow<AiSummariserResult<String>> = flow {
        emit(AiSummariserResult.Loading)
        try {
            val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readText()
                }
            } ?: throw Exception("Failed to open input stream for URI: $uri")

            val backupData = json.decodeFromString<AppBackupData>(jsonString)

            articleDao.clearAllData()

            articleDao.insertAllArticles(backupData.articles)
            articleDao.insertAllSummaries(backupData.summaries)
            emit(AiSummariserResult.Success("Data imported successfully"))
        } catch (e: Exception) {
            emit(AiSummariserResult.Error(e))
        }
    }
}