package com.shekharhandigol.aiarticlesummarizer.ui.savedArticleScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shekharhandigol.aiarticlesummarizer.core.AiSummariserResult
import com.shekharhandigol.aiarticlesummarizer.core.ArticleUiModel
import com.shekharhandigol.aiarticlesummarizer.domain.DeleteArticleByIdUseCase
import com.shekharhandigol.aiarticlesummarizer.domain.GetAllFavouriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedArticleScreenViewModel @Inject constructor(
    private val deleteArticleByIdUseCase: DeleteArticleByIdUseCase,
    private val getAllFavouriteUseCase: GetAllFavouriteUseCase
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<SavedArticleScreenUiStates>(SavedArticleScreenUiStates.Loading)
    val uiState = _uiState.asStateFlow()

    fun getArticlesFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            getAllFavouriteUseCase().collect { articles ->
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

    fun deleteArticleById(articleId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteArticleByIdUseCase(articleId)
        }
    }
}


sealed interface SavedArticleScreenUiStates {
    data object Loading : SavedArticleScreenUiStates
    data object Error : SavedArticleScreenUiStates
    data class Success(val articles: List<ArticleUiModel>) : SavedArticleScreenUiStates
}