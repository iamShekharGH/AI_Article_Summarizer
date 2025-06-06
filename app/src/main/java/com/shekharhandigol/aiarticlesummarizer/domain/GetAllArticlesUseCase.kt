package com.shekharhandigol.aiarticlesummarizer.domain

import com.shekharhandigol.aiarticlesummarizer.core.AiSummariserResult
import com.shekharhandigol.aiarticlesummarizer.core.ArticleUiModel
import com.shekharhandigol.aiarticlesummarizer.data.repoFiles.AiArticleSummarizerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetAllArticlesUseCase @Inject constructor(
    private val repository: AiArticleSummarizerRepository
) : NoInputUseCase<Flow<AiSummariserResult<List<ArticleUiModel>>>> {

    override suspend fun invoke(): Flow<AiSummariserResult<List<ArticleUiModel>>> {
        return repository.getAllArticles()
    }
}

class DeleteArticleUseCase @Inject constructor(
    private val repository: AiArticleSummarizerRepository
) : NoOutputUseCase<Int> {

    override suspend fun invoke(input: Int) {
        repository.deleteArticleById(input)
    }
}