package com.shekharhandigol.aiarticlesummarizer.ui.articlesHome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shekharhandigol.aiarticlesummarizer.core.AiSummariserResult
import com.shekharhandigol.aiarticlesummarizer.core.ArticleUiModel
import com.shekharhandigol.aiarticlesummarizer.domain.DeleteArticleByIdUseCase
import com.shekharhandigol.aiarticlesummarizer.domain.GetAllArticlesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticlesListScreenViewModel @Inject constructor(
    private val deleteArticleUseCase: DeleteArticleByIdUseCase,
    private val getArticlesUseCase: GetAllArticlesUseCase,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<ArticlesHomeScreenUiState>(ArticlesHomeScreenUiState.Loading)
    val uiState: MutableStateFlow<ArticlesHomeScreenUiState> = _uiState

    fun getArticlesFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            getArticlesUseCase().collect { articles ->
                when (articles) {
                    is AiSummariserResult.Error -> {
                        _uiState.value = ArticlesHomeScreenUiState.Error
                    }

                    AiSummariserResult.Loading -> {
                        _uiState.value = ArticlesHomeScreenUiState.Loading
                    }

                    is AiSummariserResult.Success -> {
                        _uiState.value = ArticlesHomeScreenUiState.Success(articles.data)
                    }
                }
            }
        }
    }

    fun deleteArticleById(articleId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteArticleUseCase(articleId)
        }
    }


}

sealed interface ArticlesHomeScreenUiState {
    data class Success(val articles: List<ArticleUiModel>) : ArticlesHomeScreenUiState
    data object Loading : ArticlesHomeScreenUiState
    data object Error : ArticlesHomeScreenUiState
}