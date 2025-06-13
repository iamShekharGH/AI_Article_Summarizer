package com.shekharhandigol.aiarticlesummarizer.ui.articlesHome

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shekharhandigol.aiarticlesummarizer.core.ArticleUiModel
import com.shekharhandigol.aiarticlesummarizer.ui.common.ErrorUi
import com.shekharhandigol.aiarticlesummarizer.ui.common.LoadingUi
import com.shekharhandigol.aiarticlesummarizer.util.getDayOfMonthSuffix
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun MainArticleListScreen(
    onArticleClick: (Int) -> Unit = {},
) {
    val viewModel: ArticlesListScreenViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getArticlesFromDb()
    }
    when (val state = uiState.value) {
        ArticlesHomeScreenUiState.Error -> {
            ErrorUi()
        }

        ArticlesHomeScreenUiState.Loading -> {
            LoadingUi()

        }

        is ArticlesHomeScreenUiState.Success -> {
            ArticleListScreen(
                articles = state.articles,
                onArticleClick = onArticleClick,
                onDeleteClick = { viewModel.deleteArticleById(it.articleId) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleListScreen(
    articles: List<ArticleUiModel>,
    onArticleClick: (Int) -> Unit = {},
    onDeleteClick: (ArticleUiModel) -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        if (articles.isEmpty()) {
            Text(
                text = "No articles summarized yet.",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(modifier = Modifier.padding(8.dp)) {
                items(articles) { article ->
                    ArticleListItem(
                        article = article,
                        onArticleClick = onArticleClick,
                        onDeleteClick = onDeleteClick
                    )
                }
            }
        }
    }

}

@Composable
fun ArticleListItem(
    article: ArticleUiModel,
    onArticleClick: (Int) -> Unit,
    onDeleteClick: (ArticleUiModel) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = {
            onArticleClick(article.articleId)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp), // Added weight and padding
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(
                    onClick = { onDeleteClick(article) }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        "Delete"
                    )
                }

            }
            TagsRow(tags = article.tags)

            Spacer(modifier = Modifier.height(8.dp))
            val formattedDate = SimpleDateFormat(
                "d'${getDayOfMonthSuffix(article.date)}' MMMM yyyy",
                Locale.getDefault()
            ).format(Date(article.date))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Summarized on: $formattedDate",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsRow(tags: List<String>) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(tags.size) { tag ->
            Text(
                text = tags[tag],
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(4.dp)
            )
        }
    }
}

@Preview
@Composable
fun ArticleListScreenPreview() {
    ArticleListScreen(
        articles = dummyArticles,
        onArticleClick = { articleId ->
            // Dummy action for preview
            println("Article clicked: $articleId")
        }
    )
}

val dummyArticles = listOf(
    ArticleUiModel(
        1,
        title = "The Future of AI",
        articleUrl = "https://example.com/ai",
        favouriteArticles = true,
        tags = listOf("AI", "Technology", "Future"),
        typeOfSummary = "Key Points",
        imageUrl = "https://example.com/ai_image.jpg"
    ),
    ArticleUiModel(
        2,
        title = "Quantum Computing Explained",
        articleUrl = "https://example.com/quantum",
        tags = listOf("Quantum Computing", "Physics", "Technology"),
        typeOfSummary = "Detailed Summary",
        imageUrl = "https://example.com/quantum_image.jpg"
    ),
    ArticleUiModel(
        3,
        title = "Sustainable Living Tips",
        articleUrl = "https://example.com/sustainable",
        tags = listOf(
            "Sustainability",
            "Lifestyle",
            "Environment",
            "Lifestyle",
            "Environment",
            "Lifestyle",
            "Environment"
        ),
        typeOfSummary = "Key Points",
        imageUrl = "https://example.com/sustainable_image.jpg"
    )
)