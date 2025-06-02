package com.shekharhandigol.aiarticlesummarizer.ui.summaryScreen

import android.content.Intent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.shekharhandigol.aiarticlesummarizer.core.ArticleUiModel
import com.shekharhandigol.aiarticlesummarizer.core.ArticleWithSummaryUiModel
import com.shekharhandigol.aiarticlesummarizer.core.SummaryUiModel
import com.shekharhandigol.aiarticlesummarizer.util.getDayOfMonthSuffix
import com.shekharhandigol.aiarticlesummarizer.util.simpleMarkdownToAnnotatedString
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    articleWithSummaries: ArticleWithSummaryUiModel,
    sheetState: SheetState = SheetState(
        skipPartiallyExpanded = false,
        initialValue = SheetValue.Expanded,
        confirmValueChange = { true },
        skipHiddenState = true,
        density = Density(1f),
    ),
    onDismiss: () -> Unit,
    addToFavorites: (Int, Boolean) -> Unit,
    deleteArticle: (Int) -> Unit,
    showFavoriteButton: Boolean = true
) {

    val summary = articleWithSummaries.summaryUiModel.first()
    val article = articleWithSummaries.articleUiModel
    val context = LocalContext.current
    val intent = Intent(Intent.ACTION_VIEW, article.articleUrl.toUri())
    ModalBottomSheet(
        onDismissRequest = { onDismiss.invoke() },
        sheetState = sheetState,
        shape = ShapeDefaults.Medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LazyColumn {
                item {
                    if (article.imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(data = article.imageUrl)
                                .apply(block = fun ImageRequest.Builder.() {
                                    crossfade(true)
                                    // You can add placeholder/error drawables here if needed
                                }).build(),
                            contentDescription = "Article Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                item {
                    Text(
                        text = article.title.trim(),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                item {
                    val formattedDate = SimpleDateFormat(
                        "d'${getDayOfMonthSuffix(article.date)}' MMMM yyyy",
                        Locale.getDefault()
                    ).format(Date(article.date))
                    Text(
                        text = "Summarized on: $formattedDate",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                item {
                    Text(
                        text = article.articleUrl,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline
                        ),
                        modifier = Modifier
                            .clickable {
                                context.startActivity(intent)
                            }
                            .padding(bottom = 16.dp)
                    )
                }

                item {
                    Text(
                        text = "Summary",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = article.typeOfSummary,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                    )
                    Text(
                        text = simpleMarkdownToAnnotatedString(summary.summaryText),
                        style = MaterialTheme.typography.bodyLarge,

                    )
                }

                item {
                    var showOriginalText by remember { mutableStateOf(false) }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showOriginalText = !showOriginalText }
                            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                            .padding(4.dp)
                    ) {
                        Text(
                            text = if (showOriginalText) "Hide Original Text" else "Show Original Text",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.secondary,
                                textDecoration = TextDecoration.Underline
                            ),
                            modifier = Modifier

                                .padding(vertical = 8.dp)
                        )
                        Icon(
                            imageVector = if (showOriginalText) Icons.Filled.KeyboardArrowUp else Icons.Filled.ArrowDropDown,
                            contentDescription = "Favorite",
                            tint = if (showOriginalText) MaterialTheme.colorScheme.secondary else Color.Gray
                        )
                    }

                    if (showOriginalText) {
                        Text(
                            text = "Original Text",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                        )
                        Text(
                            text = summary.ogText,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }


                item {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        onClick = {
                            addToFavorites(article.articleId, article.favouriteArticles)
                            onDismiss.invoke()
                        },
                        enabled = showFavoriteButton
                    ) {
                        if (article.favouriteArticles) {
                            Text(
                                text = "Remove from Favorites",
                                modifier = Modifier.padding(4.dp),
                                style = MaterialTheme.typography.labelLarge
                            )
                            Icon(
                                imageVector = Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorite"
                            )
                        } else {
                            Text(
                                text = "Add to Favorites",
                                modifier = Modifier.padding(4.dp),
                                style = MaterialTheme.typography.labelLarge
                            )
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = "Favorite"
                            )
                        }

                    }
                }

                item {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        onClick = {
                            deleteArticle(article.articleId)
                            onDismiss.invoke()
                        },
                        enabled = showFavoriteButton
                    ) {
                        if (article.favouriteArticles) {
                            Text(
                                text = "Delete",
                                modifier = Modifier.padding(4.dp),
                                style = MaterialTheme.typography.labelLarge
                            )
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete"
                            )
                        } else {
                            Text(
                                text = "Delete",
                                modifier = Modifier.padding(4.dp),
                                style = MaterialTheme.typography.labelLarge
                            )
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Delete"
                            )
                        }

                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun SummaryScreenPreview() {
    SummaryScreen(
        articleWithSummaries = articleSummariesDummyData,
        onDismiss = {},
        sheetState = SheetState(
            skipPartiallyExpanded = false,
            initialValue = SheetValue.Expanded,
            confirmValueChange = { true },
            skipHiddenState = true,
            density = Density(1f),
        ),
        addToFavorites = { _, _ -> },
        deleteArticle = {}
    )
}

val articleSummariesDummyData = ArticleWithSummaryUiModel(
    articleUiModel = ArticleUiModel(
        title = "Kotlin’s Builder Functions: " +
                "A Better Way to Create " +
                "Lists, " +
                "Maps, " +
                "Strings & Sets",
        articleUrl = "https://medium.com/@shekharhandigol/kotlins-builder-functions",
        favouriteArticles = false,
        tags = listOf("Kotlin", "Programming", "Android"),
        typeOfSummary = "Detailed",
        imageUrl = "https://miro.medium.com/v2/resize:fit:1400/1*1Z3Z3Z3Z3Z3Z3Z3Z3Z3Z3A.png"
    ),
    summaryUiModel = listOf(
        SummaryUiModel(
            articleId = 777,
            summaryText = "Kotlin's builder functions simplify the creation of common data structures. buildList {} creates immutable lists from mutable operations within a lambda, with primitive-optimized versions like buildIntList {}. buildString {} offers a concise way to build strings using a StringBuilder in a lambda. buildSet {} constructs immutable sets, with type-specific options like buildIntSet {} (note: order is not guaranteed). buildMap {} facilitates immutable map creation, including specialized versions like buildIntIntMap {} for primitives. These standard library features reduce boilerplate and enhance code readability for object construction. Other libraries also provide similar builder utilities.",
            ogText = "This is the original text of the article. It's much longer and more detailed than the summary. It goes into depth about Kotlin's builder functions, providing code examples and explanations for each type: buildList, buildString, buildSet, and buildMap. The article also discusses the benefits of using these functions, such as improved code readability and reduced boilerplate. It might also touch upon performance considerations and compare these builders to traditional ways of creating collections or strings.",

        )
    )
)