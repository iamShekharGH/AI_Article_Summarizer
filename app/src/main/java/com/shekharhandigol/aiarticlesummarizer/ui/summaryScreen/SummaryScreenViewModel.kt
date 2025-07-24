package com.shekharhandigol.aiarticlesummarizer.ui.summaryScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shekharhandigol.aiarticlesummarizer.core.AiSummariserResult
import com.shekharhandigol.aiarticlesummarizer.core.ArticleWithSummaryUiModel
import com.shekharhandigol.aiarticlesummarizer.core.SummaryType
import com.shekharhandigol.aiarticlesummarizer.core.SummaryUiModel
import com.shekharhandigol.aiarticlesummarizer.domain.AddToFavoritesUseCase
import com.shekharhandigol.aiarticlesummarizer.domain.ArticleSummaryInput
import com.shekharhandigol.aiarticlesummarizer.domain.ArticleWithSummariesUseCase
import com.shekharhandigol.aiarticlesummarizer.domain.DeleteArticleByIdUseCase
import com.shekharhandigol.aiarticlesummarizer.domain.GenerateTagsFromTextTagUseCase
import com.shekharhandigol.aiarticlesummarizer.domain.SaveArticleToDbUseCase
import com.shekharhandigol.aiarticlesummarizer.domain.SaveSummaryToDbUseCase
import com.shekharhandigol.aiarticlesummarizer.domain.SummarizeForExistingArticleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SummaryScreenViewModel @Inject constructor(
    private val articleWithSummariesUseCase: ArticleWithSummariesUseCase,
    private val saveArticleToDbUseCase: SaveArticleToDbUseCase,
    private val favoritesUseCase: AddToFavoritesUseCase,
    private val deleteArticleUseCase: DeleteArticleByIdUseCase,
    private val getArticleWithSummariesUseCase: ArticleWithSummariesUseCase,
    private val generateTagsFromTextTagUseCase: GenerateTagsFromTextTagUseCase,
    private val summarizeForExistingArticleUseCase: SummarizeForExistingArticleUseCase,
    private val saveSummaryToDbUseCase: SaveSummaryToDbUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ArticleSummaryState>(ArticleSummaryState.EmptyState)
    val uiState = _uiState.asStateFlow()

    private val _getNewSummary =
        MutableStateFlow<ReSummariseArticleUIState>(ReSummariseArticleUIState.Initial)
    val getNewSummary = _getNewSummary.asStateFlow()

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
            val summary = input.summaryUiModel.first()
            if (input.articleUiModel.tags.isEmpty()) {
                generateTagsFromTextTagUseCase(summary.summaryText).collect {
                    saveArticleToDb(input.copy(articleUiModel = input.articleUiModel.copy(tags = it)))
                }
            }
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

    fun summarizeText(summaryType: SummaryType, articleContent: String) {
        (uiState.value as? ArticleSummaryState.Success)?.let { currentState ->
            val currentArticle = currentState.data
            val existingSummary =
                currentArticle.summaryUiModel.find { it.summaryType == summaryType }
            if (existingSummary != null) {
                _getNewSummary.value =
                    ReSummariseArticleUIState.Success(existingSummary.summaryText)
                return
            }
        }

        viewModelScope.launch {
            summarizeForExistingArticleUseCase(
                ArticleSummaryInput(
                    summaryType.prompt,
                    articleContent
                )
            ).collect { result ->
                when (result) {
                    is AiSummariserResult.Error -> {
                        _getNewSummary.value = ReSummariseArticleUIState.Error(
                            result.exception.message ?: "Unknown error"
                        )
                    }

                    AiSummariserResult.Loading -> {
                        _getNewSummary.value = ReSummariseArticleUIState.Loading
                    }

                    is AiSummariserResult.Success -> {
                        _getNewSummary.value =
                            ReSummariseArticleUIState.Success(result.data)
                        (uiState.value as? ArticleSummaryState.Success)?.let { currentState ->
                            val currentArticle = currentState.data
                            val newSummary = currentArticle.summaryUiModel.first().copy(
                                summaryText = result.data,
                                summaryType = summaryType
                            )
                            saveSummaryToDb(newSummary)
                        }
                    }
                }
            }
        }
    }

    private fun saveSummaryToDb(summaryUiModel: SummaryUiModel) {
        viewModelScope.launch(Dispatchers.IO) {
            saveSummaryToDbUseCase(summaryUiModel).collect { result ->
                when (result) {
                    is AiSummariserResult.Error -> {
                        Log.i(
                            "saveSummaryToDb",
                            "saveSummaryToDb Error: ${result.exception.message}"
                        )
                    }

                    AiSummariserResult.Loading -> {
                        Log.i("saveSummaryToDb", "saveSummaryToDb Loading")
                    }

                    is AiSummariserResult.Success -> {
                        Log.i("saveSummaryToDb", "saveSummaryToDb Success: ${result.data}")
                    }
                }
            }
        }
    }

    fun getArticleWithSummaries(articleId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            articleWithSummariesUseCase(articleId).collect { result ->
                when (result) {
                    is AiSummariserResult.Error -> {
                        _uiState.value =
                            ArticleSummaryState.Error(result.exception.message.toString())
                    }

                    AiSummariserResult.Loading -> {
                        _uiState.value = ArticleSummaryState.Loading
                    }

                    is AiSummariserResult.Success -> {
                        _uiState.value = ArticleSummaryState.Success(result.data)
                    }
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

sealed interface ReSummariseArticleUIState {
    data class Error(val error: String) : ReSummariseArticleUIState
    data class Success(val summary: String) : ReSummariseArticleUIState
    data object Initial : ReSummariseArticleUIState
    data object Loading : ReSummariseArticleUIState
}