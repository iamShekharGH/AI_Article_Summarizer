package com.shekharhandigol.aiarticlesummarizer.data.repoFiles


import android.net.Uri
import com.shekharhandigol.aiarticlesummarizer.core.AiSummariserResult
import com.shekharhandigol.aiarticlesummarizer.domain.UseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExportDataUseCase @Inject constructor(
    private val repository: AiArticleSummarizerRepository
) : UseCase<Uri, Flow<AiSummariserResult<String>>> {
    override suspend fun invoke(input: Uri): Flow<AiSummariserResult<String>> {
        return repository.exportDataToFile(input)
    }
}


class ImportDataUseCase @Inject constructor(
    private val repository: AiArticleSummarizerRepository
) : UseCase<Uri, Flow<AiSummariserResult<String>>> {
    override suspend fun invoke(input: Uri): Flow<AiSummariserResult<String>> {
        return repository.importDataFromFile(input)
    }
}