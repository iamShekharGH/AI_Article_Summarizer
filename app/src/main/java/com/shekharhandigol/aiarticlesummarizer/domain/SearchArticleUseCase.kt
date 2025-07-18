package com.shekharhandigol.aiarticlesummarizer.domain

import com.shekharhandigol.aiarticlesummarizer.core.AiSummariserResult
import com.shekharhandigol.aiarticlesummarizer.core.ArticleUiModel
import com.shekharhandigol.aiarticlesummarizer.data.repoFiles.AiArticleSummarizerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchArticleUseCase @Inject constructor(
    private val aiArticleSummarizerRepository: AiArticleSummarizerRepository
) : UseCase<String, Flow<AiSummariserResult<List<ArticleUiModel>>>> {

    override suspend fun invoke(input: String): Flow<AiSummariserResult<List<ArticleUiModel>>> {
        return aiArticleSummarizerRepository.searchArticles(input)
    }
}