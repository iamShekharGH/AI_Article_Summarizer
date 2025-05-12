package com.shekharhandigol.aiarticlesummarizer.ui.summaryScreen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shekharhandigol.aiarticlesummarizer.database.Article
import com.shekharhandigol.aiarticlesummarizer.database.ArticleWithSummaries
import com.shekharhandigol.aiarticlesummarizer.database.Summary
import com.shekharhandigol.aiarticlesummarizer.util.getDayOfMonthSuffix
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    articleWithSummaries: ArticleWithSummaries,
    sheetState: SheetState = SheetState(
        skipPartiallyExpanded = true,
        initialValue = SheetValue.Expanded,
        confirmValueChange = { true },
        skipHiddenState = true,
        density = Density(1f),
    ),
    onDismiss: () -> Unit,
    addToFavorites: (Int, Boolean) -> Unit,
    showFavoriteButton: Boolean = true
) {

    val summary = articleWithSummaries.summaries.first()
    val article = articleWithSummaries.article
    val context = LocalContext.current
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.articleUrl))
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
                    Text(
                        text = article.title.trim(),
                        style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold),
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
                        style = TextStyle(fontSize = 12.sp, fontStyle = FontStyle.Italic),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                item {
                    Text(
                        text = article.articleUrl,
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Blue,
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
                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = summary.summaryText,
                        style = TextStyle(fontSize = 16.sp, lineHeight = 24.sp)
                    )
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
                            Text(text = "Remove from Favorites", modifier = Modifier.padding(4.dp))
                            Icon(
                                imageVector = Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorite"
                            )
                        } else {
                            Text(text = "Add to Favorites", modifier = Modifier.padding(4.dp))
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = "Favorite"
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
            skipPartiallyExpanded = true,
            initialValue = SheetValue.Expanded,
            confirmValueChange = { true },
            skipHiddenState = true,
            density = Density(1f),
        ),
        addToFavorites = { _, _ -> }
    )
}

val articleSummariesDummyData = ArticleWithSummaries(
    article = Article(
        title = "Kotlin’s Builder Functions: " +
                "A Better Way to Create " +
                "Lists, " +
                "Maps, " +
                "Strings & Sets",
        articleUrl = "https://medium.com/@shekharhandigol/kotlins-builder-functions"
    ),
    summaries = listOf(
        Summary(
            articleId = 777,
            summaryText = "Kotlin's builder functions simplify the creation of common data structures. buildList {} creates immutable lists from mutable operations within a lambda, with primitive-optimized versions like buildIntList {}. buildString {} offers a concise way to build strings using a StringBuilder in a lambda. buildSet {} constructs immutable sets, with type-specific options like buildIntSet {} (note: order is not guaranteed). buildMap {} facilitates immutable map creation, including specialized versions like buildIntIntMap {} for primitives. These standard library features reduce boilerplate and enhance code readability for object construction. Other libraries also provide similar builder utilities."
        )
    )
)