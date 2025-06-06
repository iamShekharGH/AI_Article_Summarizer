package com.shekharhandigol.aiarticlesummarizer.domain

import com.shekharhandigol.aiarticlesummarizer.core.AiSummariserResult
import com.shekharhandigol.aiarticlesummarizer.core.ArticleWithSummaryUiModel
import com.shekharhandigol.aiarticlesummarizer.data.repoFiles.AiArticleSummarizerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArticleWithSummariesUseCase @Inject constructor(
    private val repository: AiArticleSummarizerRepository
) : UseCase<Int, Flow<AiSummariserResult<ArticleWithSummaryUiModel>>> {

    override suspend fun invoke(input: Int): Flow<AiSummariserResult<ArticleWithSummaryUiModel>> {
        return repository.getArticleWithSummaries(input)
    }
}

@Singleton
class AddToFavoritesUseCase @Inject constructor(
    private val repository: AiArticleSummarizerRepository
) : NoOutputUseCase<Pair<Int, Boolean>> {

    override suspend fun invoke(input: Pair<Int, Boolean>) {
        repository.favouriteThisArticle(input.first, input.second)
    }
}