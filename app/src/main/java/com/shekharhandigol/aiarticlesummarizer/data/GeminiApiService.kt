package com.shekharhandigol.aiarticlesummarizer.data

import android.graphics.Bitmap
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.shekharhandigol.aiarticlesummarizer.BuildConfig
import com.shekharhandigol.aiarticlesummarizer.data.datastore.DatastoreDao
import com.shekharhandigol.aiarticlesummarizer.util.GeminiModelName
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiApiService @Inject constructor(
    private val datastoreDao: DatastoreDao
) {

    private suspend fun getSelectedModel(): GenerativeModel {
        val modelNameValue = datastoreDao.geminiModelNameFlow.firstOrNull()?.value
            ?: GeminiModelName.GEMINI_1_5_FLASH.value
        return GenerativeModel(
            modelName = modelNameValue,
            apiKey = BuildConfig.apiKey
        )
    }
    private fun getFirebaseModel(): com.google.firebase.ai.GenerativeModel {
        val model = Firebase.ai(backend = GenerativeBackend.vertexAI())
            .generativeModel(GeminiModelName.GEMINI_2_0_FLASH.value)
        return model
    }

    suspend fun sendPromptFirebase(prompt: String): String? {
        return try {
            val response = getFirebaseModel().generateContent(prompt)
            response.text
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("GeminiApiService", "sendPromptFirebase: ${e.message}")

            "Could not generate response"
        }
    }


    suspend fun sendPromptWithImage(bitmap: Bitmap, prompt: String): String? {
        return try {
            val response = getSelectedModel().generateContent(
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
            val response = getSelectedModel().generateContent(
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