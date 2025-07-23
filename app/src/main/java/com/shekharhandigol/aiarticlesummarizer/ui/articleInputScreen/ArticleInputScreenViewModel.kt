package com.shekharhandigol.aiarticlesummarizer.ui.articleInputScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shekharhandigol.aiarticlesummarizer.core.AiSummariserResult
import com.shekharhandigol.aiarticlesummarizer.core.ArticleWithSummaryUiModel
import com.shekharhandigol.aiarticlesummarizer.core.GeminiJsoupResponseUiModel
import com.shekharhandigol.aiarticlesummarizer.data.mappers.toArticleWithSummaryUiModel
import com.shekharhandigol.aiarticlesummarizer.domain.SaveArticleToDbUseCase
import com.shekharhandigol.aiarticlesummarizer.domain.SummarizeArticleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleInputScreenViewModel @Inject constructor(
    private val summarizeArticleUseCase: SummarizeArticleUseCase,
    private val saveArticleToDbUseCase: SaveArticleToDbUseCase
) : ViewModel() {

    private val _summaryText =
        MutableStateFlow<ArticleInputScreenUIState>(ArticleInputScreenUIState.Initial(text = "Your Results Status will show up here."))
    val summaryText = _summaryText.asStateFlow()

    fun summarizeText(url: String) {
        viewModelScope.launch {
            summarizeArticleUseCase(input = url).collect { result ->

                when (result) {
                    is AiSummariserResult.Error -> {
                        _summaryText.value = ArticleInputScreenUIState.Error(
                            result.exception.message ?: "Unknown error"
                        )
                    }

                    AiSummariserResult.Loading -> {
                        _summaryText.value = ArticleInputScreenUIState.Loading
                    }

                    is AiSummariserResult.Success -> {
                        _summaryText.value =
                            ArticleInputScreenUIState.UrlSummarisedSuccessfully(
                                result.data
                            )
                    }
                }
            }
        }
    }

    fun saveArticleToDb(articleWithSummaryUiModel: ArticleWithSummaryUiModel) {
        viewModelScope.launch {
            saveArticleToDbUseCase(articleWithSummaryUiModel)
                .collect { result ->
                    when (result) {
                        is AiSummariserResult.Error -> {
                            _summaryText.value = ArticleInputScreenUIState.Error(
                                result.exception.message ?: "Unknown error"
                            )
                        }

                        AiSummariserResult.Loading -> {
                            _summaryText.value = ArticleInputScreenUIState.Loading

                        }

                        is AiSummariserResult.Success -> {
                            _summaryText.value =
                                ArticleInputScreenUIState.SavedToDbSuccessfully(result.data)
                        }
                    }
                }
        }
    }

    fun getArticleWithSummaryObj(): ArticleWithSummaryUiModel? {
        val screenStateValue = summaryText.value
        return if (screenStateValue is ArticleInputScreenUIState.UrlSummarisedSuccessfully) {
            screenStateValue.geminiJsoupResponseUiModel.toArticleWithSummaryUiModel()
        } else {
            null
        }

    }

    fun resetToInitial() {
        _summaryText.value =
            ArticleInputScreenUIState.Initial(text = "Your Results Status will show up here.")
    }


}

sealed class ArticleInputScreenUIState {
    data class Initial(val text: String) : ArticleInputScreenUIState()
    data class UrlSummarisedSuccessfully(val geminiJsoupResponseUiModel: GeminiJsoupResponseUiModel) :
        ArticleInputScreenUIState()

    data class SavedToDbSuccessfully(val id: Long) : ArticleInputScreenUIState()
    data class Error(val text: String) : ArticleInputScreenUIState()
    data object Loading : ArticleInputScreenUIState()
}