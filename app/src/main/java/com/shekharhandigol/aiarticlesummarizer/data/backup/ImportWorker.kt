package com.shekharhandigol.aiarticlesummarizer.data.backup

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.shekharhandigol.aiarticlesummarizer.core.AiSummariserResult
import com.shekharhandigol.aiarticlesummarizer.data.repoFiles.AiArticleSummarizerRepository
import com.shekharhandigol.aiarticlesummarizer.util.KEY_FILE_URI
import com.shekharhandigol.aiarticlesummarizer.util.KEY_IMPORT_STATUS
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class ImportWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: AiArticleSummarizerRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val fileUriString = inputData.getString(KEY_FILE_URI)
        if (fileUriString == null) {
            return Result.failure()
        }
        val fileUri = fileUriString.toUri()
        val importResult = repository.importDataFromFile(fileUri).first()
        return when (importResult) {
            is AiSummariserResult.Error -> {
                Log.e(
                    "ImportWorker",
                    "Import failed: ${importResult.exception.message}",
                    importResult.exception
                )
                Result.failure(workDataOf(KEY_IMPORT_STATUS to "Import failed: ${importResult.exception.message}"))
            }

            AiSummariserResult.Loading -> {
                Log.w("ImportWorker", "Import worker received loading state unexpectedly.")
                Result.retry()
            }

            is AiSummariserResult.Success -> {
                Log.d("ImportWorker", "Import successful.")
                Result.success(workDataOf(KEY_IMPORT_STATUS to "Import successful!"))

            }
        }
    }

}
