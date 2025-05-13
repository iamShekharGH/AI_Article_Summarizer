package com.shekharhandigol.aiarticlesummarizer.data.repoFiles


sealed class AiSummariserResult<out T> {
    data class Success<out T>(val data: T) : AiSummariserResult<T>()
    data class Error(val exception: Exception) : AiSummariserResult<Nothing>()
    data object Loading : AiSummariserResult<Nothing>()
}
