package com.shekharhandigol.aiarticlesummarizer.ui.articleInputScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shekharhandigol.aiarticlesummarizer.core.AiSummariserResult
import com.shekharhandigol.aiarticlesummarizer.core.ArticleUiModel
import com.shekharhandigol.aiarticlesummarizer.core.ArticleWithSummaryUiModel
import com.shekharhandigol.aiarticlesummarizer.core.GeminiJsoupResponseUiModel
import com.shekharhandigol.aiarticlesummarizer.core.SummaryUiModel
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

    fun summarizeText(text: String) {
        viewModelScope.launch {
            summarizeArticleUseCase(url = text).collect { result ->

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

    /*fun summarizeArticleWithPrompt(prompt: String, text: String) {
        viewModelScope.launch {
            repository.summarizeArticleWithPrompt(prompt = prompt, text = text).collect { result ->
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
    }*/

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
        if (screenStateValue is ArticleInputScreenUIState.UrlSummarisedSuccessfully) {
            val data = screenStateValue.geminiJsoupResponseUiModel
            val articleUiModel = ArticleUiModel(
                title = data.title,
                articleUrl = "",
                favouriteArticles = false,
                imageUrl = data.imageUrl,
                typeOfSummary = "MEDIUM"
            )
            val summaryUiModel = SummaryUiModel(
                summaryText = data.onSummarise,
                ogText = data.toSummarise,
                articleId = -1
            )
            return ArticleWithSummaryUiModel(
                articleUiModel = articleUiModel,
                summaryUiModel = listOf(summaryUiModel)
            )
        } else {
            return null
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