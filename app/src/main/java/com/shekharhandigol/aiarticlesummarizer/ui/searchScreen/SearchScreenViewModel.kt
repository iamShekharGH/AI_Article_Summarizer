package com.shekharhandigol.aiarticlesummarizer.ui.searchScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shekharhandigol.aiarticlesummarizer.data.AiArticleSummarizerRepository
import com.shekharhandigol.aiarticlesummarizer.data.Result
import com.shekharhandigol.aiarticlesummarizer.database.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val aiArticleSummarizerRepository: AiArticleSummarizerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchScreenUiStates>(SearchScreenUiStates.Initial)
    val uiState = _uiState.asStateFlow()

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private fun searchArticles(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            aiArticleSummarizerRepository.searchArticles(query).collect { articles ->
                when (articles) {
                    is Result.Error -> {
                        _uiState.value = SearchScreenUiStates.Error
                    }

                    Result.Loading -> {
                        _uiState.value = SearchScreenUiStates.Loading
                    }

                    is Result.Success -> {
                        if (articles.data.isEmpty()) _uiState.value = SearchScreenUiStates.Error
                        else _uiState.value = SearchScreenUiStates.Success(articles.data)
                    }
                }

            }

        }
    }

    fun onQueryChange(text: String) {
        _query.value = text
        if (text.isNotEmpty()) searchArticles(text)
        else _uiState.value = SearchScreenUiStates.Initial
    }

}

sealed class SearchScreenUiStates {
    data object Initial : SearchScreenUiStates()
    data object Loading : SearchScreenUiStates()
    data object Error : SearchScreenUiStates()
    data class Success(val articles: List<Article>) : SearchScreenUiStates()
}