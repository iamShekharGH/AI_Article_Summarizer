package com.shekharhandigol.aiarticlesummarizer.ui.homeScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import com.shekharhandigol.aiarticlesummarizer.SharedUrl
import com.shekharhandigol.aiarticlesummarizer.ui.common.ErrorUi
import com.shekharhandigol.aiarticlesummarizer.ui.common.LoadingUi
import com.shekharhandigol.aiarticlesummarizer.ui.homeScreen.Destinations.*
import com.shekharhandigol.aiarticlesummarizer.ui.homeScreen.navHost.HomeScreenNavHost
import com.shekharhandigol.aiarticlesummarizer.ui.summaryScreen.ArticlePassInformation.*
import com.shekharhandigol.aiarticlesummarizer.ui.summaryScreen.MainSummaryScreen
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, url: SharedUrl) {
    val viewModel: HomeScreenViewModel = hiltViewModel()
    val sheetState = rememberModalBottomSheetState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val homeScreenUiStates by viewModel.articleWithSummaries.collectAsStateWithLifecycle()

    if (url is SharedUrl.Url) {
        LaunchedEffect(url.url) {
            viewModel.summarizeText(url.url)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { paddingValues ->

        Surface(modifier = Modifier.padding(paddingValues)) {
            HomeScreenNavHost(
                navController = navController,
                onArticleClick = viewModel::articleClicked,
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
                ErrorUi()
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
                LoadingUi()
            }

            is HomeScreenUiStates.Success -> {
                MainSummaryScreen(
                    articlePassInformation = ArticleId(state.articleWithSummaryUiModel.articleUiModel.articleId),
                    onDismiss = {
                        scope.launch { sheetState.hide() }
                        viewModel.resetState()
                    },
                    openWebView = { url: String ->
                        navController.navigate(WebView(url))
                    }
                )
            }

            is HomeScreenUiStates.ShowLavarisArticle -> {
                MainSummaryScreen(
                    articlePassInformation = ArticleObject(state.articleWithSummaries),
                    onDismiss = {
                        scope.launch { sheetState.hide() }
                        viewModel.resetState()
                    },
                    openWebView = { url: String ->
                        navController.navigate(WebView(url))
                    }
                )
            }

            is HomeScreenUiStates.ArticleClicked -> {
                MainSummaryScreen(
                    articlePassInformation = ArticleId(state.articleId),
                    onDismiss = {
                        scope.launch { sheetState.hide() }
                        viewModel.resetState()
                    },
                    openWebView = { url: String ->
                        navController.navigate(WebView(url))
                    }
                )
            }

            HomeScreenUiStates.Idle -> {

            }
        }
    }
}

