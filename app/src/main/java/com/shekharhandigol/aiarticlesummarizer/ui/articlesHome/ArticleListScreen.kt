package com.shekharhandigol.aiarticlesummarizer.ui.articlesHome

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shekharhandigol.aiarticlesummarizer.database.Article


@Composable
fun MainArticleListScreen(

) {
    val vm: ArticlesListScreenViewModel = hiltViewModel()

    ArticleListScreen()
}

@Composable
fun ArticleListScreen(
    articles: List<Article> = dummyArticles,
    onArticleClick: (Int) -> Unit = {}, // Added callback for article click
) {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        if (articles.isEmpty()) {
            // Display a message if the list is empty
            Text(
                text = "No articles summarized yet.  Use the + button to add one.",
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(modifier = Modifier.padding(8.dp)) {
                items(articles) { article ->
                    ArticleListItem(
                        article = article,
                        onArticleClick = onArticleClick
                    ) // Pass the callback
                }
            }
        }
    }

}

@Composable
fun ArticleListItem(article: Article, onArticleClick: (Int) -> Unit) {  // Added callback
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = {
            onArticleClick(article.articleId)
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = article.title,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            val formattedDate = article.date
            Text(
                text = "Summarized on: $formattedDate",
                style = TextStyle(fontSize = 12.sp)
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
    Article(
        1,
        "The Future of AI",
        "https://example.com/ai",
        summaryText = "This is a summary of the Future of AI article."
    ),
    Article(
        2,
        "Quantum Computing Explained",
        "https://example.com/quantum",
        summaryText = "This is a summary of the Quantum Computing Explained article."
    ), // Yesterday
    Article(
        3,
        "Sustainable Living Tips",
        "https://example.com/sustainable",
        summaryText = "This is a summary of the Sustainable Living Tips article."
    ) // Two days ago
)