package com.shekharhandigol.aiarticlesummarizer.data

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.shekharhandigol.aiarticlesummarizer.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiApiService @Inject constructor() {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey
    )

    suspend fun sendPromptWithImage(bitmap: Bitmap, prompt: String): String? {
        return try {
            val response = generativeModel.generateContent(
                content {
                    image(bitmap)
                    text(prompt)
                }
            )
            response.text

        } catch (e: Exception) {
            e.printStackTrace()
            "Could not generate response."
        }
    }

    suspend fun sendPrompt(prompt: String): String? {
        return try {
            val response = generativeModel.generateContent(
                content {
                    text(prompt)
                }
            )
            response.text

        } catch (e: Exception) {
            e.printStackTrace()
            "Could not generate response."
        }
    }
}