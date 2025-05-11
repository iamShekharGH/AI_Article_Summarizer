package com.shekharhandigol.aiarticlesummarizer.ui.homeScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.shekharhandigol.aiarticlesummarizer.ui.articleInputScreen.MainArticleInputScreen
import com.shekharhandigol.aiarticlesummarizer.ui.articlesHome.MainArticleListScreen
import com.shekharhandigol.aiarticlesummarizer.ui.savedArticleScreen.SavedArticlesScreen
import com.shekharhandigol.aiarticlesummarizer.ui.searchScreen.LocalSearchScreen
import com.shekharhandigol.aiarticlesummarizer.ui.settings.SettingsScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text("Summarized Articles") })
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {

            }) {
                Icon(Icons.Filled.Add, contentDescription = "Summarize Article")
            }
        }
    ) { paddingValues ->
        Surface(modifier = Modifier.padding(paddingValues)) {
            HomeScreenNavHost(navController = navController)
        }
    }
}


@Composable
fun HomeScreenNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Destinations.MainHome) {
        navigation<Destinations.MainHome>(startDestination = Destinations.Home) {

            composable<Destinations.Home> { MainArticleListScreen() }
            composable<Destinations.Search> { MainArticleInputScreen() }
            composable<Destinations.List> { LocalSearchScreen(onArticleClick = {}, query = "", onQueryChange = { }) }
            composable<Destinations.LocalSearch> { SavedArticlesScreen() }
            composable<Destinations.Settings> { SettingsScreen() }
        }

    }
}
