package com.shekharhandigol.aiarticlesummarizer.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shekharhandigol.aiarticlesummarizer.data.repoFiles.AiArticleSummarizerRepository
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
    private val aiArticleSummarizerRepository: AiArticleSummarizerRepository
) : ViewModel() {

    private val _promptSettings = MutableStateFlow(SummaryLength.MEDIUM)
    val promptSettings = _promptSettings.asStateFlow()

    private val _darkMode = MutableStateFlow(false)
    val darkMode = _darkMode.asStateFlow()

    private val _geminiModel = MutableStateFlow(GeminiModelName.GEMINI_1_5_FLASH)
    val geminiModel = _geminiModel.asStateFlow()


    fun saveSummariesPromptSettings(summaryLength: SummaryLength) {
        viewModelScope.launch(Dispatchers.IO) {
            aiArticleSummarizerRepository.savePromptSettings(summaryLength)
        }
    }

    fun getPromptSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            aiArticleSummarizerRepository.getPromptSettings().collect { result ->
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
            aiArticleSummarizerRepository.saveDarkModeValue(setDarkMode)
        }
    }

    fun getDarkModeValue() {
        viewModelScope.launch(Dispatchers.IO) {
            aiArticleSummarizerRepository.getDarkModeValue().collect { result ->
                _darkMode.value = result
            }
        }
    }

    private fun saveGeminiModel(modelName: GeminiModelName) {
        viewModelScope.launch(Dispatchers.IO) {
            aiArticleSummarizerRepository.saveGeminiModel(modelName)
        }
    }

    fun getGeminiModelName() {
        viewModelScope.launch(Dispatchers.IO) {
            aiArticleSummarizerRepository.geminiModelNameFlow().collect { result ->
                _geminiModel.value = result
            }
        }
    }
}