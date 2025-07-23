package com.shekharhandigol.aiarticlesummarizer.ui.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shekharhandigol.aiarticlesummarizer.core.AiSummariserResult
import com.shekharhandigol.aiarticlesummarizer.core.ArticleWithSummaryUiModel
import com.shekharhandigol.aiarticlesummarizer.data.mappers.toArticleWithSummaryUiModel
import com.shekharhandigol.aiarticlesummarizer.domain.SummarizeArticleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val summarizeArticleUseCase: SummarizeArticleUseCase
) : ViewModel() {

    private val _articleWithSummaries =
        MutableStateFlow<HomeScreenUiStates>(HomeScreenUiStates.Idle)
    val articleWithSummaries = _articleWithSummaries.asStateFlow()

    fun articleClicked(articleId: Int) {
        _articleWithSummaries.value = HomeScreenUiStates.ArticleClicked(articleId)
    }


    fun summarizeText(url: String) {
        viewModelScope.launch {
            summarizeArticleUseCase(input = url).collect { result ->
                when (result) {
                    is AiSummariserResult.Error -> {
                        _articleWithSummaries.value = HomeScreenUiStates.Error
                    }

                    AiSummariserResult.Loading -> {
                        _articleWithSummaries.value = HomeScreenUiStates.Loading
                    }

                    is AiSummariserResult.Success -> {
                        _articleWithSummaries.value =
                            HomeScreenUiStates.ShowLavarisArticle(result.data.toArticleWithSummaryUiModel())
                    }
                }
            }
        }
    }

    fun resetState() {
        _articleWithSummaries.value = HomeScreenUiStates.Idle
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

    data class ArticleClicked(val articleId: Int) : HomeScreenUiStates
    data class ShowLavarisArticle(val articleWithSummaries: ArticleWithSummaryUiModel) :
        HomeScreenUiStates
}