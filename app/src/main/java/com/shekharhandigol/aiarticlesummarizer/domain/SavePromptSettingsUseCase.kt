package com.shekharhandigol.aiarticlesummarizer.domain

import com.shekharhandigol.aiarticlesummarizer.core.GeminiModelName
import com.shekharhandigol.aiarticlesummarizer.core.SummaryType
import com.shekharhandigol.aiarticlesummarizer.data.repoFiles.AiArticleSummarizerRepository
import com.shekharhandigol.aiarticlesummarizer.util.AppThemeOption
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SavePromptSettingsUseCase @Inject constructor(
    private val aiArticleSummarizerRepository: AiArticleSummarizerRepository
) : NoOutputUseCase<SummaryType> {

    override suspend fun invoke(input: SummaryType) {
        aiArticleSummarizerRepository.savePromptSettings(input)
    }
}

class GetPromptSettingsUseCase @Inject constructor(
    private val aiArticleSummarizerRepository: AiArticleSummarizerRepository
) : NoInputUseCase<Flow<SummaryType>> {

    override suspend fun invoke(): Flow<SummaryType> {
        return aiArticleSummarizerRepository.getPromptSettings()
    }
}

class SaveGeminiModelUseCase @Inject constructor(
    private val aiArticleSummarizerRepository: AiArticleSummarizerRepository
) : NoOutputUseCase<GeminiModelName> {
    override suspend fun invoke(input: GeminiModelName) {
        aiArticleSummarizerRepository.saveGeminiModel(input)
    }
}

class GetGeminiModelUseCase @Inject constructor(
    private val aiArticleSummarizerRepository: AiArticleSummarizerRepository
) : NoInputUseCase<Flow<GeminiModelName>> {
    override suspend fun invoke(): Flow<GeminiModelName> =
        aiArticleSummarizerRepository.geminiModelNameFlow()
}

class GetThemeNameUseCase @Inject constructor(
    private val aiArticleSummarizerRepository: AiArticleSummarizerRepository
) : NoInputUseCase<Flow<AppThemeOption>> {
    override suspend fun invoke(): Flow<AppThemeOption> =
        aiArticleSummarizerRepository.getThemeName()
}

class SaveThemeNameUseCase @Inject constructor(
    private val aiArticleSummarizerRepository: AiArticleSummarizerRepository
) : NoOutputUseCase<AppThemeOption> {
    override suspend fun invoke(input: AppThemeOption) {
        aiArticleSummarizerRepository.saveThemeName(input)
    }
}
