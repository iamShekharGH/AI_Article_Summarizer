package com.shekharhandigol.aiarticlesummarizer.domain


import android.net.Uri
import androidx.work.ExistingWorkPolicy
import androidx.work.Data.Builder
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.shekharhandigol.aiarticlesummarizer.core.AiSummariserResult
import com.shekharhandigol.aiarticlesummarizer.data.backup.ExportWorker
import com.shekharhandigol.aiarticlesummarizer.data.backup.ImportWorker
import com.shekharhandigol.aiarticlesummarizer.data.repoFiles.AiArticleSummarizerRepository
import com.shekharhandigol.aiarticlesummarizer.util.KEY_FILE_URI
import com.shekharhandigol.aiarticlesummarizer.util.KEY_IS_AUTOMATIC
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExportDataUseCase @Inject constructor(
    private val repository: AiArticleSummarizerRepository
) : UseCase<Uri, Flow<AiSummariserResult<String>>> {
    override suspend fun invoke(input: Uri): Flow<AiSummariserResult<String>> {
        return repository.exportDataToFile(input)
    }
}

class ScheduleExportUseCase @Inject constructor(
    private val workManager: WorkManager
) : NoOutputUseCase<Params> {
    override suspend fun invoke(input: Params) {
        val dataBuilder = Builder()
        dataBuilder.putBoolean(KEY_IS_AUTOMATIC, input.isAutomatic)
        if (!input.isAutomatic && input.fileUri != null) {
            dataBuilder.putString(KEY_FILE_URI, input.fileUri.toString())
        }
        val inputData = dataBuilder.build()

        val exportRequest = OneTimeWorkRequestBuilder<ExportWorker>()
            .setInputData(inputData)
            .build()
        val uniqueWorkName = if (input.isAutomatic) "DailyDataExport" else "ManualExportDataWork"

        workManager.enqueueUniqueWork(
            uniqueWorkName,
            ExistingWorkPolicy.REPLACE,
            exportRequest
        )
    }
}

class ScheduleImportUseCase @Inject constructor(
    private val workManager: WorkManager // Injected by Hilt
) : NoOutputUseCase<Uri> {

    override suspend operator fun invoke(input: Uri) {
        val inputData = workDataOf(KEY_FILE_URI to input.toString())
        val importRequest = OneTimeWorkRequestBuilder<ImportWorker>()
            .setInputData(inputData)
            .build()

        workManager.enqueueUniqueWork(
            "ImportDataWork",
            ExistingWorkPolicy.REPLACE,
            importRequest
        )
    }
}


class ImportDataUseCase @Inject constructor(
    private val repository: AiArticleSummarizerRepository
) : UseCase<Uri, Flow<AiSummariserResult<String>>> {
    override suspend fun invoke(input: Uri): Flow<AiSummariserResult<String>> {
        return repository.importDataFromFile(input)
    }
}

data class Params(val fileUri: Uri?, val isAutomatic: Boolean)