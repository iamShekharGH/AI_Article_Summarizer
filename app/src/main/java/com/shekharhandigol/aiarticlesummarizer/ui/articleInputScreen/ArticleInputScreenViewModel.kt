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


    private val _summaryText = MutableStateFlow("Your Results will show up here.")
    val summaryText = _summaryText.asStateFlow()

    fun summarizeText(text: String) {
        viewModelScope.launch {
            repository.summarizeArticle(url = text).collect { result ->
                when (result) {
                    is Result.Error -> {
                        _summaryText.value = result.exception.message ?: "Unknown error"
                    }

                    Result.Loading -> {
                        _summaryText.value = "Loading..."
                    }

                    is Result.Success -> {
                        _summaryText.value = result.data
                    }

                    is Result.PartialSuccess -> {
                        _summaryText.value = result.data
                    }
                }
            }
        }
    }
}