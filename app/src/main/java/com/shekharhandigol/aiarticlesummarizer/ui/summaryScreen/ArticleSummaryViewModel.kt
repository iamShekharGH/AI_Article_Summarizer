package com.shekharhandigol.aiarticlesummarizer.ui.summaryScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shekharhandigol.aiarticlesummarizer.core.AiSummariserResult
import com.shekharhandigol.aiarticlesummarizer.core.ArticleWithSummaryUiModel
import com.shekharhandigol.aiarticlesummarizer.domain.AddToFavoritesUseCase
import com.shekharhandigol.aiarticlesummarizer.domain.ArticleWithSummariesUseCase
import com.shekharhandigol.aiarticlesummarizer.domain.DeleteArticleByIdUseCase
import com.shekharhandigol.aiarticlesummarizer.domain.SaveArticleToDbUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleSummaryViewModel @Inject constructor(
    private val saveArticleToDbUseCase: SaveArticleToDbUseCase,
    private val favoritesUseCase: AddToFavoritesUseCase,
    private val deleteArticleUseCase: DeleteArticleByIdUseCase,
    private val getArticleWithSummariesUseCase: ArticleWithSummariesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ArticleSummaryState>(ArticleSummaryState.EmptyState)
    val uiState = _uiState.asStateFlow()

    fun saveArticleToDb(input: ArticleWithSummaryUiModel) {
        viewModelScope.launch(Dispatchers.IO) {
            saveArticleToDbUseCase(input).collect { result ->
                when (result) {
                    is AiSummariserResult.Error -> {
                        _uiState.value =
                            ArticleSummaryState.Error(result.exception.message.toString())
                    }

                    AiSummariserResult.Loading -> {
                        _uiState.value = ArticleSummaryState.Loading
                    }

                    is AiSummariserResult.Success -> {
                        fetchArticleSummary(result.data.toInt())
                    }
                }
            }
        }
    }

    fun showArticleSummary(input: ArticleWithSummaryUiModel) {
        viewModelScope.launch {
            _uiState.value = ArticleSummaryState.Success(input)
        }
    }

    fun favouriteThisArticle(articleId: Int, setThisAsFavouriteState: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            favoritesUseCase(Pair(articleId, setThisAsFavouriteState))
            fetchArticleSummary(articleId)
        }
    }

    fun deleteArticle(articleId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteArticleUseCase(articleId)
        }
    }

    private suspend fun fetchArticleSummary(articleId: Int) {
        getArticleWithSummariesUseCase(articleId).collect {
            when (it) {
                is AiSummariserResult.Error -> {
                    _uiState.value = ArticleSummaryState.Error(it.exception.message.toString())
                }

                AiSummariserResult.Loading -> {
                    _uiState.value = ArticleSummaryState.Loading
                }

                is AiSummariserResult.Success -> {
                    _uiState.value = ArticleSummaryState.Success(it.data)
                }
            }
        }
    }
}

sealed interface ArticleSummaryState {
    data class Success(val data: ArticleWithSummaryUiModel) : ArticleSummaryState
    data class Error(val message: String) : ArticleSummaryState
    data object Loading : ArticleSummaryState
    data object EmptyState : ArticleSummaryState
}