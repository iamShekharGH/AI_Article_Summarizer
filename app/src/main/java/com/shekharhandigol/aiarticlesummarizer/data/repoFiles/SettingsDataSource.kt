package com.shekharhandigol.aiarticlesummarizer.data.repoFiles

import com.shekharhandigol.aiarticlesummarizer.data.datastore.DatastoreDao
import com.shekharhandigol.aiarticlesummarizer.util.GeminiModelName
import com.shekharhandigol.aiarticlesummarizer.util.SummaryLength
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsDataSource @Inject constructor(
    private val datastoreDao: DatastoreDao
) {

    suspend fun savePromptSettings(summaryLength: SummaryLength) {
        datastoreDao.savePromptSettings(summaryLength)
    }

    fun getPromptSettings(): Flow<SummaryLength> = datastoreDao.promptSettingsFlow


    fun getDarkModeValue(): Flow<Boolean> = datastoreDao.darkModeFlow

    suspend fun saveDarkModeValue(darkMode: Boolean) {
        datastoreDao.setDarkMode(darkMode)

    }

    suspend fun saveGeminiModel(modelName: GeminiModelName) {
        datastoreDao.setGeminiModelName(modelName)
    }

    fun geminiModelNameFlow(): Flow<GeminiModelName> = datastoreDao.geminiModelNameFlow
}