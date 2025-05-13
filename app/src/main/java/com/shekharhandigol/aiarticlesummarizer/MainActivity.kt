package com.shekharhandigol.aiarticlesummarizer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.shekharhandigol.aiarticlesummarizer.ui.homeScreen.HomeScreen
import com.shekharhandigol.aiarticlesummarizer.ui.theme.AIArticleSummarizerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedUrl = handleIncomingIntent(intent)
        val url = if (sharedUrl == null) {
            SharedUrl.None
        } else {
            SharedUrl.Url(sharedUrl)
        }

        setContent {
            AIArticleSummarizerTheme {
                val navController = rememberNavController()
                HomeScreen(navController, url)
            }
        }
    }

    private fun handleIncomingIntent(intent: Intent?): String? {
        if (intent?.action != Intent.ACTION_SEND || intent.type != "text/plain") {
            return null
        }

        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT) ?: return null

        return sharedText.takeIf {
            try {
                Uri.parse(it).scheme?.startsWith("http") == true
            } catch (e: Exception) {
                false
            }
        }
    }
}

sealed interface SharedUrl {
    data object None : SharedUrl
    data class Url(val url: String) : SharedUrl
}

