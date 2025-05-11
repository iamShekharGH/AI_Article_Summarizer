package com.shekharhandigol.aiarticlesummarizer.ui.articleInputScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shekharhandigol.aiarticlesummarizer.data.AiArticleSummarizerRepository
import com.shekharhandigol.aiarticlesummarizer.data.Result
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
        MutableStateFlow<ArticleInputScreenUIState>(ArticleInputScreenUIState.Initial(text = "Your Results will show up here."))
    val summaryText = _summaryText.asStateFlow()

    fun summarizeText(text: String) {
        viewModelScope.launch {
            repository.summarizeArticle(url = text).collect { result ->
                when (result) {
                    is Result.Error -> {
                        _summaryText.value = ArticleInputScreenUIState.Error(
                            result.exception.message ?: "Unknown error"
                        )
                    }

                    Result.Loading -> {
                        _summaryText.value = ArticleInputScreenUIState.Loading
                    }

                    is Result.Success -> {
                        _summaryText.value =
                            ArticleInputScreenUIState.Success(result.data1, result.data2)
                    }

                    is Result.PartialSuccess -> {
                        _summaryText.value =
                            ArticleInputScreenUIState.Success(result.data1, result.data2)
                    }
                }
            }
        }
    }

    fun saveArticleToDb(url: String, summary: String, title: String) {
        viewModelScope.launch {
            repository.insertArticle(url = url, summary = summary, title = title)
                .collect { result ->
                    when (result) {
                        is Result.Error -> {
                            _summaryText.value = ArticleInputScreenUIState.Error(
                                result.exception.message ?: "Unknown error"
                            )
                        }

                        Result.Loading -> {
                            _summaryText.value = ArticleInputScreenUIState.Loading

                        }

                        is Result.PartialSuccess, is Result.Success -> {
                            _summaryText.value =
                                ArticleInputScreenUIState.Initial(text = "Article saved!")
                        }
                    }
                }
        }
    }
}

sealed class ArticleInputScreenUIState {
    data class Initial(val text: String) : ArticleInputScreenUIState()
    data class Success(val title: String, val description: String) : ArticleInputScreenUIState()
    data class Error(val text: String) : ArticleInputScreenUIState()
    data object Loading : ArticleInputScreenUIState()
}