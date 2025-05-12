package com.shekharhandigol.aiarticlesummarizer.ui.homeScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.shekharhandigol.aiarticlesummarizer.database.ArticleWithSummaries
import com.shekharhandigol.aiarticlesummarizer.ui.articleInputScreen.MainArticleInputScreen
import com.shekharhandigol.aiarticlesummarizer.ui.articlesHome.MainArticleListScreen
import com.shekharhandigol.aiarticlesummarizer.ui.savedArticleScreen.MainFavouriteArticlesScreen
import com.shekharhandigol.aiarticlesummarizer.ui.searchScreen.LocalSearchScreen
import com.shekharhandigol.aiarticlesummarizer.ui.settings.SettingsScreen
import com.shekharhandigol.aiarticlesummarizer.ui.summaryScreen.SummaryScreen
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val viewModel: HomeScreenViewModel = hiltViewModel()
    val sheetState = rememberModalBottomSheetState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val homeScreenUiStates by viewModel.articleWithSummaries.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text("Summarized Articles") })
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {

            }) {
                Icon(Icons.Filled.Add, contentDescription = "Summarize Article")
            }
        }
    ) { paddingValues ->

        Surface(modifier = Modifier.padding(paddingValues)) {
            HomeScreenNavHost(
                navController = navController,
                onArticleClick = viewModel::getArticleWithSummaries,
                showJustSummarizedText = viewModel::showJustSummarizedText
            )
        }
        when (val state = homeScreenUiStates) {
            HomeScreenUiStates.Error -> {
                LaunchedEffect(snackbarHostState) {
                    snackbarHostState.showSnackbar(
                        message = "Error Loading Article",
                        duration = SnackbarDuration.Short,
                        withDismissAction = true,
                        actionLabel = "Dismiss",
                    )
                }

            }

            HomeScreenUiStates.Loading -> {
                LaunchedEffect(snackbarHostState) {
                    snackbarHostState.showSnackbar(
                        message = "Loading...",
                        duration = SnackbarDuration.Short,
                        withDismissAction = true,
                        actionLabel = "Dismiss",
                    )
                }
            }

            is HomeScreenUiStates.Success -> {
                SummaryScreen(
                    articleWithSummaries = state.articleWithSummaries,
                    sheetState = sheetState,
                    onDismiss = {
                        scope.launch { sheetState.hide() }
                        viewModel.resetState()
                    },
                    addToFavorites = viewModel::addToFavorites
                )
            }

            is HomeScreenUiStates.ShowLavarisArticle -> {
                SummaryScreen(
                    articleWithSummaries = state.articleWithSummaries,
                    sheetState = sheetState,
                    onDismiss = {
                        scope.launch { sheetState.hide() }
                        viewModel.resetState()
                    },
                    addToFavorites = { _, _ -> },
                    showFavoriteButton = false
                )
            }

            HomeScreenUiStates.Idle -> {

            }


        }


    }
}


@Composable
fun HomeScreenNavHost(
    navController: NavHostController,
    onArticleClick: (Int) -> Unit,
    showJustSummarizedText: (ArticleWithSummaries) -> Unit
) {
    NavHost(navController = navController, startDestination = Destinations.MainHome) {
        navigation<Destinations.MainHome>(startDestination = Destinations.Home) {

            composable<Destinations.Home> { MainArticleListScreen(onArticleClick) }
            composable<Destinations.Search> {
                MainArticleInputScreen(
                    onArticleClick,
                    showJustSummarizedText
                )
            }
            composable<Destinations.List> {
                LocalSearchScreen(
                    onArticleClick = onArticleClick,
                    onDeleteClick = {}
                )
            }
            composable<Destinations.FavouriteList> { MainFavouriteArticlesScreen(onArticleClick) }
            composable<Destinations.Settings> { SettingsScreen() }
        }

    }
}
