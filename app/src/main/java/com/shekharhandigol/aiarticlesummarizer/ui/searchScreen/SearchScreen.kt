package com.shekharhandigol.aiarticlesummarizer.ui.searchScreen


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shekharhandigol.aiarticlesummarizer.database.Article
import com.shekharhandigol.aiarticlesummarizer.ui.articlesHome.ArticleListItem


@Composable
fun LocalSearchScreen(
    onArticleClick: (Int) -> Unit,
    onDeleteClick: (Article) -> Unit,
    articles: List<Article> = emptyList(),
    query: String,
    onQueryChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            TextField(
                value = query,
                onValueChange = { onQueryChange(it) },
                label = { Text("Search Articles") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (articles.isEmpty()) {
                Text(
                    text = "No articles found.",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(modifier = Modifier.padding(8.dp)) {
                    items(articles.size) { article ->
                        ArticleListItem(
                            article = articles[article],
                            onArticleClick = onArticleClick,
                            onDeleteClick = onDeleteClick
                        )
                    }
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewSearchScreenWithArticles() {
    LocalSearchScreen(
        onArticleClick = {},
        onDeleteClick = {},
        articles = listOf(
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
        ),
        query = "Sample",
        onQueryChange = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchScreenWithoutArticles() {
    LocalSearchScreen(
        onArticleClick = {},
        onDeleteClick = {},
        articles = emptyList(),
        query = "Sample",
        onQueryChange = {}
    )
}


@Composable
fun FeedbackScreen(
    onFeedbackSubmit: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            var feedback by remember { mutableStateOf("") }
            TextField(
                value = feedback,
                onValueChange = { feedback = it },
                label = { Text("Enter your feedback") },
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f) // Adjust height as needed
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onFeedbackSubmit(feedback) },
                modifier = Modifier.fillMaxWidth(),
                enabled = feedback.isNotBlank()
            ) {
                Text("Submit Feedback")
            }
        }
    }


}

@Preview(showBackground = true)
@Composable
fun PreviewFeedbackScreen() {
    FeedbackScreen(
        onFeedbackSubmit = {}
    )
}
