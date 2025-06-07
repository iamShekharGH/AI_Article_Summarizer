package com.shekharhandigol.aiarticlesummarizer.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shekharhandigol.aiarticlesummarizer.domain.GetGeminiModelUseCase
import com.shekharhandigol.aiarticlesummarizer.domain.GetPromptSettingsUseCase
import com.shekharhandigol.aiarticlesummarizer.domain.GetThemeNameUseCase
import com.shekharhandigol.aiarticlesummarizer.domain.SaveGeminiModelUseCase
import com.shekharhandigol.aiarticlesummarizer.domain.SavePromptSettingsUseCase
import com.shekharhandigol.aiarticlesummarizer.domain.SaveThemeNameUseCase
import com.shekharhandigol.aiarticlesummarizer.util.AppThemeOption
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
    private val saveGeminiModelUseCase: SaveGeminiModelUseCase,
    private val getGeminiModelUseCase: GetGeminiModelUseCase,
    private val getThemeNameUseCase: GetThemeNameUseCase,
    private val saveThemeNameUseCase: SaveThemeNameUseCase
) : ViewModel() {

    private val _promptSettings = MutableStateFlow(SummaryLength.MEDIUM_SUMMARY)
    val promptSettings = _promptSettings.asStateFlow()

    private val _geminiModel = MutableStateFlow(GeminiModelName.GEMINI_1_5_FLASH)
    val geminiModel = _geminiModel.asStateFlow()

    private val _themeName = MutableStateFlow(AppThemeOption.SYSTEM_DEFAULT)
    val themeName = _themeName.asStateFlow()


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

    fun getThemeName() {
        viewModelScope.launch(Dispatchers.IO) {
            getThemeNameUseCase().collect { result ->
                _themeName.value = result
            }
        }
    }

    /*
    //TODO move theme chooser to this setting page.
    fun setThemeName(themeName: AppThemeOption) {
        viewModelScope.launch(Dispatchers.IO) {
            _themeName.value = themeName
            saveThemeNameUseCase(themeName)
        }
    }*/
}