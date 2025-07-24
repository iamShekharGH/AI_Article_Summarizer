package com.shekharhandigol.aiarticlesummarizer.data.repoFiles

import com.shekharhandigol.aiarticlesummarizer.core.GeminiModelName
import com.shekharhandigol.aiarticlesummarizer.core.SummaryType
import com.shekharhandigol.aiarticlesummarizer.data.datastore.DatastoreDao
import com.shekharhandigol.aiarticlesummarizer.util.AppThemeOption
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsDataSource @Inject constructor(
    private val datastoreDao: DatastoreDao
) {

    suspend fun savePromptSettings(summaryLength: SummaryType) {
        datastoreDao.savePromptSettings(summaryLength)
    }

    fun getPromptSettings(): Flow<SummaryType> = datastoreDao.promptSettingsFlow


    suspend fun saveGeminiModel(modelName: GeminiModelName) {
        datastoreDao.setGeminiModelName(modelName)
    }

    fun geminiModelNameFlow(): Flow<GeminiModelName> = datastoreDao.geminiModelNameFlow

    suspend fun saveThemeName(themeName: AppThemeOption) {
        datastoreDao.setSelectedAppTheme(themeName)
    }

    fun getThemeName(): Flow<AppThemeOption> = datastoreDao.selectedAppTheme
}