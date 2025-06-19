package com.shekharhandigol.aiarticlesummarizer.domain

import com.shekharhandigol.aiarticlesummarizer.core.AiSummariserResult
import com.shekharhandigol.aiarticlesummarizer.core.ArticleUiModel
import com.shekharhandigol.aiarticlesummarizer.data.repoFiles.AiArticleSummarizerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class GetAllTagsUseCase @Inject constructor(
    private val repository: AiArticleSummarizerRepository
) : NoInputUseCase<Flow<AiSummariserResult<List<String>>>> {

    override suspend fun invoke(): Flow<AiSummariserResult<List<String>>> {
        return repository.getAllTags()
    }
}

@Singleton
class GetArticlesByTagUseCase @Inject constructor(
    private val repository: AiArticleSummarizerRepository
) : UseCase<String, Flow<AiSummariserResult<List<ArticleUiModel>>>> {

    override suspend fun invoke(tag: String): Flow<AiSummariserResult<List<ArticleUiModel>>> {
        return repository.getArticlesByTag(tag)
    }
}

@Singleton
class GenerateTagsFromTextTagUseCase @Inject constructor(
    private val repository: AiArticleSummarizerRepository
) : UseCase<String, Flow<List<String>>> {

    override suspend fun invoke(input: String): Flow<List<String>> {
        return flowOf(repository.generateTagsFromText(input))
    }
}