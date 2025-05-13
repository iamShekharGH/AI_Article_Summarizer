package com.shekharhandigol.aiarticlesummarizer.ui.savedArticleScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shekharhandigol.aiarticlesummarizer.data.repoFiles.AiArticleSummarizerRepository
import com.shekharhandigol.aiarticlesummarizer.data.repoFiles.AiSummariserResult
import com.shekharhandigol.aiarticlesummarizer.database.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedArticleScreenViewModel @Inject constructor(
    private val articleRepository: AiArticleSummarizerRepository
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<SavedArticleScreenUiStates>(SavedArticleScreenUiStates.Loading)
    val uiState = _uiState.asStateFlow()

    fun getArticlesFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            articleRepository.getAllFavoriteArticles().collect { articles ->
                when (articles) {
                    is AiSummariserResult.Error -> {
                        _uiState.value = SavedArticleScreenUiStates.Error
                    }

                    AiSummariserResult.Loading -> {
                        _uiState.value = SavedArticleScreenUiStates.Loading
                    }

                    is AiSummariserResult.Success -> {
                        _uiState.value = SavedArticleScreenUiStates.Success(articles.data)
                    }
                }
            }
        }
    }

    fun deleteArticleById(article: Article) {
        viewModelScope.launch(Dispatchers.IO) {
            articleRepository.deleteArticleById(article.articleId)
        }
    }
}


sealed interface SavedArticleScreenUiStates {
    data object Loading : SavedArticleScreenUiStates
    data object Error : SavedArticleScreenUiStates
    data class Success(val articles: List<Article>) : SavedArticleScreenUiStates
}