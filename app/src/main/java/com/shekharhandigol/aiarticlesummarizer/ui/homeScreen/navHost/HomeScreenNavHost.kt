package com.shekharhandigol.aiarticlesummarizer.ui.homeScreen.navHost

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.shekharhandigol.aiarticlesummarizer.core.ArticleWithSummaryUiModel
import com.shekharhandigol.aiarticlesummarizer.ui.articleInputScreen.MainArticleInputScreen
import com.shekharhandigol.aiarticlesummarizer.ui.articlesHome.MainArticleListScreen
import com.shekharhandigol.aiarticlesummarizer.ui.homeScreen.Destinations
import com.shekharhandigol.aiarticlesummarizer.ui.login.MainLoginScreen
import com.shekharhandigol.aiarticlesummarizer.ui.savedArticleScreen.MainFavouriteArticlesScreen
import com.shekharhandigol.aiarticlesummarizer.ui.searchScreen.LocalSearchScreen
import com.shekharhandigol.aiarticlesummarizer.ui.settings.MainSettingsScreen
import com.shekharhandigol.aiarticlesummarizer.ui.summaryScreen.uiElements.FullScreenWebView
import com.shekharhandigol.aiarticlesummarizer.ui.themeChooser.ThemeChooserScreen


@Composable
fun HomeScreenNavHost(
    navController: NavHostController,
    onArticleClick: (Int) -> Unit,
    showJustSummarizedText: (ArticleWithSummaryUiModel) -> Unit
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
                    onArticleClick = onArticleClick
                )
            }
            composable<Destinations.FavouriteList> { MainFavouriteArticlesScreen(onArticleClick) }
            composable<Destinations.Settings> {
                MainSettingsScreen(
                    openThemesChooser = {
                    navController.navigate(Destinations.ChooseTheme)
                    },
                    openSigninWithGoogle = {
                        navController.navigate(Destinations.Login)
                    })
            }
            composable<Destinations.ChooseTheme> { ThemeChooserScreen() }
            composable<Destinations.WebView> { backStackEntry ->
                val webViewDestination = backStackEntry.toRoute<Destinations.WebView>()
                FullScreenWebView(url = webViewDestination.url, onShouldClose = {
                    navController.popBackStack()
                })
            }
            composable<Destinations.Login> {
                MainLoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Destinations.Home) {
                            popUpTo(Destinations.Login) { inclusive = true }
                        }
                    }, skipLogin = {
                        navController.navigate(Destinations.Home) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                )
            }
        }

    }
}
