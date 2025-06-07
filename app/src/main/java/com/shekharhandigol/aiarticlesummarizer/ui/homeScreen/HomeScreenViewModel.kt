package com.shekharhandigol.aiarticlesummarizer.ui.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shekharhandigol.aiarticlesummarizer.core.AiSummariserResult
import com.shekharhandigol.aiarticlesummarizer.core.ArticleWithSummaryUiModel
import com.shekharhandigol.aiarticlesummarizer.domain.AddToFavoritesUseCase
import com.shekharhandigol.aiarticlesummarizer.domain.ArticleWithSummariesUseCase
import com.shekharhandigol.aiarticlesummarizer.domain.DeleteArticleByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val articleWithSummariesUseCase: ArticleWithSummariesUseCase,
    private val addArticleToFavoritesUseCase: AddToFavoritesUseCase,
    private val deleteArticleUseCase: DeleteArticleByIdUseCase
) : ViewModel() {

    private val _articleWithSummaries =
        MutableStateFlow<HomeScreenUiStates>(HomeScreenUiStates.Idle)
    val articleWithSummaries = _articleWithSummaries.asStateFlow()


    fun getArticleWithSummaries(articleId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            articleWithSummariesUseCase(articleId).collect { result ->
                when (result) {
                    is AiSummariserResult.Error -> {
                        _articleWithSummaries.value = HomeScreenUiStates.Error
                    }

                    AiSummariserResult.Loading -> {
                        _articleWithSummaries.value = HomeScreenUiStates.Loading
                    }

                    is AiSummariserResult.Success -> {
                        _articleWithSummaries.value =
                            HomeScreenUiStates.Success(result.data)
                    }
                }
            }
        }
    }

    fun resetState() {
        _articleWithSummaries.value = HomeScreenUiStates.Idle
    }

    fun deleteArticle(articleId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteArticleUseCase(articleId)
        }
    }

    fun showJustSummarizedText(articleWithSummaries: ArticleWithSummaryUiModel) {
        _articleWithSummaries.value = HomeScreenUiStates.ShowLavarisArticle(articleWithSummaries)
    }
}

sealed interface HomeScreenUiStates {
    data object Loading : HomeScreenUiStates
    data object Idle : HomeScreenUiStates
    data object Error : HomeScreenUiStates
    data class Success(val articleWithSummaryUiModel: ArticleWithSummaryUiModel) :
        HomeScreenUiStates

    data class ShowLavarisArticle(val articleWithSummaries: ArticleWithSummaryUiModel) :
        HomeScreenUiStates

}