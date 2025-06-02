package com.shekharhandigol.aiarticlesummarizer.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.shekharhandigol.aiarticlesummarizer.util.DATASTORE_DARK_MODE
import com.shekharhandigol.aiarticlesummarizer.util.DATASTORE_GEMINI_MODEL_NAME
import com.shekharhandigol.aiarticlesummarizer.util.DATASTORE_PROMPT_SETTINGS
import com.shekharhandigol.aiarticlesummarizer.util.GeminiModelName
import com.shekharhandigol.aiarticlesummarizer.util.SummaryLength
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatastoreDao @Inject constructor(private val dataStore: DataStore<Preferences>) {

    private val promptSettingsPreferenceKey = stringPreferencesKey(DATASTORE_PROMPT_SETTINGS)
    private val geminiModelNamePreferenceKey = stringPreferencesKey(DATASTORE_GEMINI_MODEL_NAME)
    private val darkModePreferenceKey = booleanPreferencesKey(DATASTORE_DARK_MODE)


    val darkModeFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[darkModePreferenceKey] == true
        }.catch { exception ->
            exception.printStackTrace()
            emit(false)
        }

    suspend fun setDarkMode(isDarkMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[darkModePreferenceKey] = isDarkMode
        }
    }


    val promptSettingsFlow: Flow<SummaryLength> = dataStore.data
        .map { preferences ->
            SummaryLength.entries.find { it.value == preferences[promptSettingsPreferenceKey] }
                ?: SummaryLength.MEDIUM
        }.catch { exception ->
            exception.printStackTrace()
            emit(SummaryLength.MEDIUM)
        }

    suspend fun savePromptSettings(length: SummaryLength) {
        dataStore.edit { preferences ->
            preferences[promptSettingsPreferenceKey] = length.value
        }
    }

    val geminiModelNameFlow: Flow<GeminiModelName> = dataStore.data
        .map { preferences ->
            GeminiModelName.entries.find { it.value == preferences[geminiModelNamePreferenceKey] }
                ?: GeminiModelName.GEMINI_1_5_FLASH
        }.catch { exception ->
            exception.printStackTrace()
            emit(GeminiModelName.GEMINI_1_5_FLASH)
        }

    suspend fun setGeminiModelName(modelName: GeminiModelName) {
        dataStore.edit { preferences ->
            preferences[geminiModelNamePreferenceKey] = modelName.value
        }
    }


}