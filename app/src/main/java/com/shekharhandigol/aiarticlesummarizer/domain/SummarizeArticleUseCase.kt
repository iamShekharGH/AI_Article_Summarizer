package com.shekharhandigol.aiarticlesummarizer.domain

import com.shekharhandigol.aiarticlesummarizer.core.AiSummariserResult
import com.shekharhandigol.aiarticlesummarizer.core.ArticleWithSummaryUiModel
import com.shekharhandigol.aiarticlesummarizer.core.GeminiJsoupResponseUiModel
import com.shekharhandigol.aiarticlesummarizer.core.SummaryUiModel
import com.shekharhandigol.aiarticlesummarizer.data.repoFiles.AiArticleSummarizerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SummarizeArticleUseCase @Inject constructor(
    private val repository: AiArticleSummarizerRepository
) : UseCase<String, Flow<AiSummariserResult<GeminiJsoupResponseUiModel>>> {

    override suspend fun invoke(input: String): Flow<AiSummariserResult<GeminiJsoupResponseUiModel>> {
        return repository.summarizeArticle(url = input)
    }
}

@Singleton
class SummarizeForExistingArticleUseCase @Inject constructor(
    private val repository: AiArticleSummarizerRepository
) : UseCase<ArticleSummaryInput, Flow<AiSummariserResult<String>>> {
    override suspend fun invoke(input: ArticleSummaryInput): Flow<AiSummariserResult<String>> {
        return repository.summarizeArticleWithPrompt(input.prompt, input.articleContent)
    }

}

@Singleton
class SaveArticleToDbUseCase @Inject constructor(
    private val repository: AiArticleSummarizerRepository
) : UseCase<ArticleWithSummaryUiModel, Flow<AiSummariserResult<Long>>> {

    override suspend fun invoke(input: ArticleWithSummaryUiModel): Flow<AiSummariserResult<Long>> {
        return repository.insertArticleWithSummary(input)
    }
}

@Singleton
class SaveSummaryToDbUseCase @Inject constructor(
    private val repository: AiArticleSummarizerRepository
) : UseCase<SummaryUiModel, Flow<AiSummariserResult<Long>>> {

    override suspend fun invoke(input: SummaryUiModel): Flow<AiSummariserResult<Long>> {
        return repository.insertSummary(input)
    }
}

data class ArticleSummaryInput(val prompt: String, val articleContent: String)