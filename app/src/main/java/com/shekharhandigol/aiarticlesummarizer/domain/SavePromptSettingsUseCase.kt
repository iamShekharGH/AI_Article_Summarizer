package com.shekharhandigol.aiarticlesummarizer.domain

import com.shekharhandigol.aiarticlesummarizer.data.repoFiles.AiArticleSummarizerRepository
import com.shekharhandigol.aiarticlesummarizer.util.AppThemeOption
import com.shekharhandigol.aiarticlesummarizer.util.GeminiModelName
import com.shekharhandigol.aiarticlesummarizer.util.SummaryLength
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SavePromptSettingsUseCase @Inject constructor(
    private val aiArticleSummarizerRepository: AiArticleSummarizerRepository
) : NoOutputUseCase<SummaryLength> {

    override suspend fun invoke(input: SummaryLength) {
        aiArticleSummarizerRepository.savePromptSettings(input)
    }
}

class GetPromptSettingsUseCase @Inject constructor(
    private val aiArticleSummarizerRepository: AiArticleSummarizerRepository
) : NoInputUseCase<Flow<SummaryLength>> {

    override suspend fun invoke(): Flow<SummaryLength> {
        return aiArticleSummarizerRepository.getPromptSettings()
    }
}

class SaveDarkModeUseCase @Inject constructor(
    private val aiArticleSummarizerRepository: AiArticleSummarizerRepository
) : NoOutputUseCase<Boolean> {
    override suspend fun invoke(input: Boolean) {
        aiArticleSummarizerRepository.saveDarkModeValue(input)
    }
}

class GetDarkModeUseCase @Inject constructor(
    private val aiArticleSummarizerRepository: AiArticleSummarizerRepository
) : NoInputUseCase<Flow<Boolean>> {
    override suspend fun invoke(): Flow<Boolean> {
        return aiArticleSummarizerRepository.getDarkModeValue()
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
