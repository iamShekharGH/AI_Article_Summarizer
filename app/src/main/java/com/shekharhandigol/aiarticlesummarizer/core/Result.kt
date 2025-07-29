package com.shekharhandigol.aiarticlesummarizer.core


sealed class AiSummariserResult<out T> {
    data class Success<out T>(val data: T) : AiSummariserResult<T>()
    data class Error(val exception: Throwable) : AiSummariserResult<Nothing>()
    data object Loading : AiSummariserResult<Nothing>()
}
