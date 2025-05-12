package com.shekharhandigol.aiarticlesummarizer.ui.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shekharhandigol.aiarticlesummarizer.data.AiArticleSummarizerRepository
import com.shekharhandigol.aiarticlesummarizer.data.Result
import com.shekharhandigol.aiarticlesummarizer.database.ArticleWithSummaries
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val aiArticleSummarizerRepository: AiArticleSummarizerRepository
) : ViewModel() {

    private val _articleWithSummaries =
        MutableStateFlow<HomeScreenUiStates>(HomeScreenUiStates.Idle)
    val articleWithSummaries = _articleWithSummaries.asStateFlow()


    fun getArticleWithSummaries(articleId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            aiArticleSummarizerRepository.getArticleWithSummaries(articleId).collect { result ->
                when (result) {
                    is Result.Error -> {
                        _articleWithSummaries.value = HomeScreenUiStates.Error
                    }

                    Result.Loading -> {
                        _articleWithSummaries.value = HomeScreenUiStates.Loading
                    }

                    is Result.Success -> {
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

    fun addToFavorites(articleId: Int, currentFavouriteState: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            aiArticleSummarizerRepository.favouriteThisArticle(articleId, currentFavouriteState)
        }
    }
}

sealed interface HomeScreenUiStates {
    data object Loading : HomeScreenUiStates
    data object Idle : HomeScreenUiStates
    data object Error : HomeScreenUiStates
    data class Success(val articleWithSummaries: ArticleWithSummaries) : HomeScreenUiStates

}