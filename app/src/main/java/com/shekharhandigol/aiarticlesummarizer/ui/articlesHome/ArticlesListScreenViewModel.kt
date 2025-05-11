package com.shekharhandigol.aiarticlesummarizer.ui.articlesHome

import androidx.lifecycle.ViewModel
import com.shekharhandigol.aiarticlesummarizer.data.AiArticleSummarizerRepository
import com.shekharhandigol.aiarticlesummarizer.database.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ArticlesListScreenViewModel @Inject constructor(
    private val articleRepository: AiArticleSummarizerRepository
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<ArticlesHomeScreenUiState>(ArticlesHomeScreenUiState.Loading)
    val uiState: MutableStateFlow<ArticlesHomeScreenUiState> = _uiState


}

sealed interface ArticlesHomeScreenUiState {
    data class Success(val articles: List<Article>) : ArticlesHomeScreenUiState
    data object Loading : ArticlesHomeScreenUiState
    data object Error : ArticlesHomeScreenUiState
}