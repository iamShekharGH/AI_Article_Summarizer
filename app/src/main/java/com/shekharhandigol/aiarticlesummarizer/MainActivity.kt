package com.shekharhandigol.aiarticlesummarizer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.net.toUri
import androidx.navigation.compose.rememberNavController
import com.shekharhandigol.aiarticlesummarizer.data.datastore.DatastoreDao
import com.shekharhandigol.aiarticlesummarizer.ui.homeScreen.HomeScreen
import com.shekharhandigol.aiarticlesummarizer.ui.theme.AIArticleSummarizerTheme
import com.shekharhandigol.aiarticlesummarizer.util.AppThemeOption
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var datastoreDao: DatastoreDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedUrl = handleIncomingIntent(intent)
        val url = if (sharedUrl == null) {
            SharedUrl.None
        } else {
            SharedUrl.Url(sharedUrl)
        }

        setContent {
            val theme by datastoreDao.selectedAppTheme.collectAsState(initial = AppThemeOption.SYSTEM_DEFAULT)

            AIArticleSummarizerTheme(
                selectedTheme = theme
            ) {
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
                it.toUri().scheme?.startsWith("http") == true
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("MainActivity", "handleIncomingIntent: ${e.message}")
                false
            }
        }
    }
}

sealed interface SharedUrl {
    data object None : SharedUrl
    data class Url(val url: String) : SharedUrl
}

