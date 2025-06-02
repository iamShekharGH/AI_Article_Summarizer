package com.shekharhandigol.aiarticlesummarizer.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shekharhandigol.aiarticlesummarizer.domain.GetDarkModeUseCase
import com.shekharhandigol.aiarticlesummarizer.domain.GetGeminiModelUseCase
import com.shekharhandigol.aiarticlesummarizer.domain.GetPromptSettingsUseCase
import com.shekharhandigol.aiarticlesummarizer.domain.SaveDarkModeUseCase
import com.shekharhandigol.aiarticlesummarizer.domain.SaveGeminiModelUseCase
import com.shekharhandigol.aiarticlesummarizer.domain.SavePromptSettingsUseCase
import com.shekharhandigol.aiarticlesummarizer.util.GeminiModelName
import com.shekharhandigol.aiarticlesummarizer.util.SummaryLength
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val savePromptSettingsUseCase: SavePromptSettingsUseCase,
    private val getPromptSettingsUseCase: GetPromptSettingsUseCase,
    private val saveDarkModeUseCase: SaveDarkModeUseCase,
    private val getDarkModeUseCase: GetDarkModeUseCase,
    private val saveGeminiModelUseCase: SaveGeminiModelUseCase,
    private val getGeminiModelUseCase: GetGeminiModelUseCase
) : ViewModel() {

    private val _promptSettings = MutableStateFlow(SummaryLength.MEDIUM)
    val promptSettings = _promptSettings.asStateFlow()

    private val _darkMode = MutableStateFlow(false)
    val darkMode = _darkMode.asStateFlow()

    private val _geminiModel = MutableStateFlow(GeminiModelName.GEMINI_1_5_FLASH)
    val geminiModel = _geminiModel.asStateFlow()


    fun saveSummariesPromptSettings(summaryLength: SummaryLength) {
        viewModelScope.launch(Dispatchers.IO) {
            savePromptSettingsUseCase(summaryLength)
        }
    }

    fun getPromptSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            getPromptSettingsUseCase().collect { result ->
                _promptSettings.value = result

            }
        }
    }

    fun setGeminiModel(modelName: GeminiModelName) {
        _geminiModel.value = modelName
        saveGeminiModel(modelName)
    }

    fun setDarkModeValue(setDarkMode: Boolean) {
        _darkMode.value = setDarkMode
        saveDarkModeValue(setDarkMode)
    }

    private fun saveDarkModeValue(setDarkMode: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            saveDarkModeUseCase(setDarkMode)
        }
    }

    fun getDarkModeValue() {
        viewModelScope.launch(Dispatchers.IO) {
            getDarkModeUseCase().collect { result ->
                _darkMode.value = result
            }
        }
    }

    private fun saveGeminiModel(modelName: GeminiModelName) {
        viewModelScope.launch(Dispatchers.IO) {
            saveGeminiModelUseCase(modelName)
        }
    }

    fun getGeminiModelName() {
        viewModelScope.launch(Dispatchers.IO) {
            getGeminiModelUseCase().collect { result ->
                _geminiModel.value = result
            }
        }
    }
}