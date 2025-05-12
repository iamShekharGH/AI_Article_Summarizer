package com.shekharhandigol.aiarticlesummarizer.ui.savedArticleScreen


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shekharhandigol.aiarticlesummarizer.database.Article
import com.shekharhandigol.aiarticlesummarizer.ui.articlesHome.ArticleListItem
import com.shekharhandigol.aiarticlesummarizer.ui.common.ErrorUi
import com.shekharhandigol.aiarticlesummarizer.ui.common.LoadingUi

@Composable
fun MainSavedArticlesScreen(
    onArticleClick: (Int) -> Unit = {},
) {

    val viewModel = hiltViewModel<SavedArticleScreenViewModel>()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getArticlesFromDb()
    }

    when (val state = uiState.value) {
        SavedArticleScreenUiStates.Error -> {
            ErrorUi()
        }

        SavedArticleScreenUiStates.Loading -> {
            LoadingUi()

        }

        is SavedArticleScreenUiStates.Success -> {
            SavedArticlesScreen(
                articles = state.articles,
                onArticleClick = onArticleClick,
                deleteArticleById = { viewModel.deleteArticleById(it) }
            )
        }
    }


}

@Composable
fun SavedArticlesScreen(
    articles: List<Article> = emptyList(),
    onArticleClick: (Int) -> Unit = {},
    deleteArticleById: (Article) -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (articles.isEmpty()) {
            Text(
                text = "No articles saved yet.",
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn(modifier = Modifier.padding(8.dp)) {
                items(articles.size) { article ->
                    ArticleListItem(
                        article = articles[article],
                        onArticleClick = onArticleClick,
                        onDeleteClick = deleteArticleById
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewSavedArticlesScreenWithData() {
    val dummyArticles = listOf(
        Article(
            articleId = 1,
            title = "Dummy Article 1",
            articleUrl = "http://example.com/article1"
        ),
        Article(
            articleId = 2,
            title = "Another Dummy Article",
            articleUrl = "http://example.com/article2"
        ),
        Article(
            articleId = 3,
            title = "Third Example Article",
            articleUrl = "http://example.com/article3"
        )

    )
    SavedArticlesScreen(
        articles = dummyArticles,
        onArticleClick = {},
        deleteArticleById = { }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewSavedArticlesScreenEmpty() {
    SavedArticlesScreen(
        articles = emptyList(),
        onArticleClick = {},
        deleteArticleById = { }
    )
}