package com.shekharhandigol.aiarticlesummarizer.data.backup

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
class SimpleAdditionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val additionService: AdditionService
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "SimpleAdditionWorker"
        const val KEY_NUM1 = "num1"
        const val KEY_NUM2 = "num2"
        const val KEY_RESULT = "result"
    }

    /*override suspend fun doWork(): Result {
        Log.d(TAG, "doWork started. Worker ID: $id")

        val num1 = inputData.getInt(KEY_NUM1, 0)
        val num2 = inputData.getInt(KEY_NUM2, 0)

        Log.d(TAG, "Received numbers: num1=$num1, num2=$num2")

        // Simulate some work
        delay(2000)

        val sum = num1 + num2
        Log.d(TAG, "Calculated sum: $num1 + $num2 = $sum")

        // Return success and output data
        return Result.success(workDataOf(KEY_RESULT to sum))
    }*/

    override suspend fun doWork(): Result {
        Log.d(TAG, "doWork started. Worker ID: $id")

        val num1 = inputData.getInt(KEY_NUM1, 0)
        val num2 = inputData.getInt(KEY_NUM2, 0)

        Log.d(TAG, "Received numbers: num1=$num1, num2=$num2")

        // Use the injected AdditionService
        val sum = additionService.add(num1, num2)

        // Simulate some work
        delay(2000)

        Log.d(TAG, "Calculated sum: $num1 + $num2 = $sum")

        return Result.success(workDataOf(KEY_RESULT to sum))
    }
}