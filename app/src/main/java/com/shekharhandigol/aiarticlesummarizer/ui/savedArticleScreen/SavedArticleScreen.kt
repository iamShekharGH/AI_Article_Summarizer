package com.shekharhandigol.aiarticlesummarizer.ui.savedArticleScreen


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shekharhandigol.aiarticlesummarizer.database.Article
import com.shekharhandigol.aiarticlesummarizer.ui.articlesHome.ArticleListItem

@Composable
fun SavedArticlesScreen(
    articles: List<Article> = emptyList(),
    onArticleClick: (Int) -> Unit = {},
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
                        onArticleClick = onArticleClick
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
            articleUrl = "http://example.com/article1",
            summaryText = "This is a short summary for article 1."
        ),
        Article(
            articleId = 2,
            title = "Another Dummy Article",
            articleUrl = "http://example.com/article2",
            summaryText = "This is a short summary for article 2."
        ),
        Article(
            articleId = 3,
            title = "Third Example Article",
            articleUrl = "http://example.com/article3",
            summaryText = "This is a short summary for article 3."
        )

    )
    SavedArticlesScreen(
        articles = dummyArticles,
        onArticleClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewSavedArticlesScreenEmpty() {
    SavedArticlesScreen(articles = emptyList(), onArticleClick = {})
}