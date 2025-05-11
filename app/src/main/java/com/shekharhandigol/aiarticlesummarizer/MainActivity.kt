package com.shekharhandigol.aiarticlesummarizer

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
        setContent {
            AIArticleSummarizerTheme {
                val navController = rememberNavController()
                HomeScreen(navController)
            }
        }
    }
}

