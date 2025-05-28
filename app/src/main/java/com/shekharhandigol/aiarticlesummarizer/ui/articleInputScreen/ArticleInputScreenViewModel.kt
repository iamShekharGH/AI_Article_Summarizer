package com.shekharhandigol.aiarticlesummarizer.ui.articleInputScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shekharhandigol.aiarticlesummarizer.data.repoFiles.AiArticleSummarizerRepository
import com.shekharhandigol.aiarticlesummarizer.data.repoFiles.AiSummariserResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleInputScreenViewModel @Inject constructor(
    private val repository: AiArticleSummarizerRepository
) : ViewModel() {


    private val _summaryText =
        MutableStateFlow<ArticleInputScreenUIState>(ArticleInputScreenUIState.Initial(text = "Your Results Status will show up here."))
    val summaryText = _summaryText.asStateFlow()

    fun summarizeText(text: String) {
        viewModelScope.launch {
            repository.summarizeArticle(url = text).collect { result ->
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
                                result.data.first,
                                result.data.second
                            )
                    }
                }
            }
        }
    }

    fun summarizeArticleWithPrompt(prompt: String, text: String) {
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
                                result.data.first,
                                result.data.second
                            )
                    }
                }
            }
        }
    }

    fun saveArticleToDb(url: String, summary: String, title: String) {
        viewModelScope.launch {
            repository.insertArticleWithSummary(url = url, title = title, summary = summary)
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

    fun resetToInitial() {
        _summaryText.value =
            ArticleInputScreenUIState.Initial(text = "Your Results Status will show up here.")
    }
}

sealed class ArticleInputScreenUIState {
    data class Initial(val text: String) : ArticleInputScreenUIState()
    data class UrlSummarisedSuccessfully(val title: String, val description: String) :
        ArticleInputScreenUIState()

    data class SavedToDbSuccessfully(val id: Long) : ArticleInputScreenUIState()
    data class Error(val text: String) : ArticleInputScreenUIState()
    data object Loading : ArticleInputScreenUIState()
}