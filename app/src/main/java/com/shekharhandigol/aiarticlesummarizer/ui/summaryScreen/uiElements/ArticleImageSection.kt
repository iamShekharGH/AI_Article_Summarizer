package com.shekharhandigol.aiarticlesummarizer.ui.summaryScreen.uiElements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.shekharhandigol.aiarticlesummarizer.R
import com.shekharhandigol.aiarticlesummarizer.core.ArticleUiModel


@Composable
fun ArticleImageSection(
    article: ArticleUiModel,
    saveArticle: () -> Unit,
    toggleFavourite: (Int, Boolean) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (article.imageUrl.isNotBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(data = article.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Article Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.placeholder),
                error = painterResource(R.drawable.placeholder),
                fallback = painterResource(R.drawable.placeholder)
            )
        }
        if (article.articleId <= 0)
            OutlinedButton(
                onClick = saveArticle,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .align(Alignment.TopStart),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            ) {
                Text(text = "Save Article")
            }
        else
            IconButton(
                onClick = { toggleFavourite(article.articleId, !article.favouriteArticles) },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                val icon =
                    if (article.favouriteArticles) Icons.Default.Favorite else Icons.Default.FavoriteBorder
                Icon(
                    imageVector = icon,
                    contentDescription = if (article.favouriteArticles) "Favourite" else "Un-Favourite",
                )
            }
    }
}

@Preview
@Composable
fun ArticleImageSectionPreview() {
    ArticleImageSection(
        article = ArticleUiModel(
            articleId = 0,
            title = "Test Article",
            imageUrl = "https://example.com/image.jpg",
            favouriteArticles = false,
            articleUrl = "https://example.com/article",
            tags = emptyList(),
            typeOfSummary = "Key Points",
        ),
        saveArticle = { },
        toggleFavourite = { _, _ -> }
    )
}

@Preview
@Composable
fun ArticleImageSectionShowItemsPreview() {
    ArticleImageSection(
        article = ArticleUiModel(
            articleId = 1,
            title = "Test Article",
            imageUrl = "https://example.com/image.jpg",
            favouriteArticles = false,
            articleUrl = "https://example.com/article",
            tags = emptyList(),
            typeOfSummary = "Key Points",
        ),
        saveArticle = { },
        toggleFavourite = { _, _ -> }
    )
}
