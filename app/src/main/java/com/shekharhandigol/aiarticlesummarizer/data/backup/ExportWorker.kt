package com.shekharhandigol.aiarticlesummarizer.data.backup

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.shekharhandigol.aiarticlesummarizer.core.AiSummariserResult
import com.shekharhandigol.aiarticlesummarizer.data.repoFiles.AiArticleSummarizerRepository
import com.shekharhandigol.aiarticlesummarizer.util.BACKUP_FILE_EXTENSION
import com.shekharhandigol.aiarticlesummarizer.util.DEFAULT_BACKUP_FILENAME_PREFIX
import com.shekharhandigol.aiarticlesummarizer.util.KEY_EXPORT_STATUS
import com.shekharhandigol.aiarticlesummarizer.util.KEY_FILE_URI
import com.shekharhandigol.aiarticlesummarizer.util.KEY_IS_AUTOMATIC
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@HiltWorker
class ExportWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: AiArticleSummarizerRepository
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "ExportWorker"
    }


    override suspend fun doWork(): Result {
        Log.i(TAG, "doWork() started for WorkRequest ID: $id")
        val isAutomatic = inputData.getBoolean(KEY_IS_AUTOMATIC, false)
        val fileUriString = inputData.getString(KEY_FILE_URI)

        val targetUri: Uri = if (isAutomatic) {
            val backupDir = File(applicationContext.getExternalFilesDir(null), "backups")
            if (!backupDir.exists()) {
                val created = backupDir.mkdirs() // Create the directory if it doesn't exist
                if (!created) {
                    Log.e(TAG, "Failed to create backup directory: ${backupDir.absolutePath}")
                    return Result.failure(workDataOf(KEY_EXPORT_STATUS to "Error: Could not create backup directory."))
                }
            }
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "${DEFAULT_BACKUP_FILENAME_PREFIX}${timestamp}${BACKUP_FILE_EXTENSION}"
            val backupFile = File(backupDir, fileName)
            Uri.fromFile(backupFile)
        } else {
            Log.d(TAG, "Manual export detected. Using provided URI string: $fileUriString")
            fileUriString?.toUri()
                ?: return Result.failure(workDataOf(KEY_EXPORT_STATUS to "Error: File URI missing."))
            fileUriString.toUri()
        }
        Log.i(TAG, "Target URI for export: $targetUri (Automatic: $isAutomatic)")
        val exportResult = repository.exportDataToFile(targetUri).first()

        return when (exportResult) {
            is AiSummariserResult.Error -> {
                Log.e(
                    "ExportWorker",
                    "Export failed: ${exportResult.exception.message}",
                    exportResult.exception // Log the full stack trace
                )
                Result.failure(workDataOf(KEY_EXPORT_STATUS to "Export failed: ${exportResult.exception.message}"))
            }

            AiSummariserResult.Loading -> {
                Log.w("ExportWorker", "Export worker received loading state unexpectedly.")
                Result.retry() // Or failure, depending on desired behavior
            }

            is AiSummariserResult.Success -> {
                Log.i("ExportWorker", "Export successful to: $targetUri")
                Result.success(workDataOf(KEY_EXPORT_STATUS to "Export successful! File: ${targetUri.lastPathSegment}"))
            }
        }

    }
}
