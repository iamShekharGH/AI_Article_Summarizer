package com.shekharhandigol.aiarticlesummarizer.ui.articlesHome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shekharhandigol.aiarticlesummarizer.data.AiArticleSummarizerRepository
import com.shekharhandigol.aiarticlesummarizer.data.Result
import com.shekharhandigol.aiarticlesummarizer.database.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticlesListScreenViewModel @Inject constructor(
    private val articleRepository: AiArticleSummarizerRepository
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<ArticlesHomeScreenUiState>(ArticlesHomeScreenUiState.Loading)
    val uiState: MutableStateFlow<ArticlesHomeScreenUiState> = _uiState

    fun getArticlesFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            articleRepository.getAllArticles().collect { articles ->
                when (articles) {
                    is Result.Error -> {
                        _uiState.value = ArticlesHomeScreenUiState.Error
                    }

                    Result.Loading -> {
                        _uiState.value = ArticlesHomeScreenUiState.Loading
                    }

                    is Result.Success -> {
                        _uiState.value = ArticlesHomeScreenUiState.Success(articles.data)
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

sealed interface ArticlesHomeScreenUiState {
    data class Success(val articles: List<Article>) : ArticlesHomeScreenUiState
    data object Loading : ArticlesHomeScreenUiState
    data object Error : ArticlesHomeScreenUiState
}