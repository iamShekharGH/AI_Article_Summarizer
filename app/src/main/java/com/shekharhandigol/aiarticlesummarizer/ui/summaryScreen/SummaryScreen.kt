package com.shekharhandigol.aiarticlesummarizer.ui.summaryScreen

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shekharhandigol.aiarticlesummarizer.core.ArticleWithSummaryUiModel
import com.shekharhandigol.aiarticlesummarizer.core.SummaryType
import com.shekharhandigol.aiarticlesummarizer.ui.common.ErrorUi
import com.shekharhandigol.aiarticlesummarizer.ui.common.LoadingUi
import com.shekharhandigol.aiarticlesummarizer.ui.summaryScreen.uiElements.ArticleImageSection
import com.shekharhandigol.aiarticlesummarizer.util.articleSummariesDummyData
import com.shekharhandigol.aiarticlesummarizer.util.getDayOfMonthSuffix
import com.shekharhandigol.aiarticlesummarizer.util.simpleMarkdownToAnnotatedString
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainSummaryScreen(
    articlePassInformation: ArticlePassInformation,
    sheetState: SheetState = SheetState(
        skipPartiallyExpanded = true,
        initialValue = SheetValue.Expanded,
        confirmValueChange = { true },
        skipHiddenState = false,
        density = Density(1f),
    ),
    onDismiss: () -> Unit,
    openWebView: (String) -> Unit
) {

    val viewModel: SummaryScreenViewModel = hiltViewModel()
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val reSummarizeState = viewModel.getNewSummary.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        when (articlePassInformation) {
            is ArticlePassInformation.ArticleObject -> viewModel.showArticleSummary(
                articlePassInformation.article
            )

            is ArticlePassInformation.ArticleId -> viewModel.getArticleWithSummaries(
                articlePassInformation.articleId
            )
        }
    }

    Box {
        when (val stateValue = state.value) {
            ArticleSummaryState.EmptyState -> {
                LoadingUi()
            }

            is ArticleSummaryState.Error -> {
                ErrorUi()
            }

            ArticleSummaryState.Loading -> {
                LoadingUi()
            }

            is ArticleSummaryState.Success -> {
                SummaryScreen(
                    articleWithSummaries = stateValue.data,
                    onDismiss = onDismiss,
                    addToFavorites = viewModel::favouriteThisArticle,
                    deleteArticle = viewModel::deleteArticle,
                    saveArticle = viewModel::saveArticleToDb,
                    sheetState = sheetState,
                    gotoWebView = openWebView,
                    reSummaryFunction = viewModel::summarizeText,
                    reSummarizeState = reSummarizeState
                )
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    articleWithSummaries: ArticleWithSummaryUiModel,
    onDismiss: () -> Unit,
    addToFavorites: (Int, Boolean) -> Unit,
    deleteArticle: (Int) -> Unit,
    saveArticle: (ArticleWithSummaryUiModel) -> Unit,
    sheetState: SheetState,
    gotoWebView: (String) -> Unit,
    reSummaryFunction: (SummaryType, String) -> Unit,
    reSummarizeState: State<ReSummariseArticleUIState>
) {

    val summary = articleWithSummaries.summaryUiModel.first()
    val article = articleWithSummaries.articleUiModel
    val context = LocalContext.current
    val intent = Intent(Intent.ACTION_VIEW, article.articleUrl.toUri())
    var expanded by remember { mutableStateOf(false) }
    var selectedSummaryType by remember { mutableStateOf(summary.summaryType) }


    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        shape = ShapeDefaults.Medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Summary",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        IconButton(
                            onClick = {
                                onDismiss()
                            }
                        ) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                        }
                    }
                    HorizontalDivider(thickness = 1.dp)
                }
                item {
                    ArticleImageSection(
                        article = article,
                        saveArticle = { saveArticle(articleWithSummaries) },
                        toggleFavourite = addToFavorites
                    )

                }
                item {
                    Text(
                        text = article.title.trim(),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                item {
                    Card {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                        ) {
                            val formattedDate = SimpleDateFormat(
                                "d'${getDayOfMonthSuffix(article.date)}' MMMM yyyy",
                                Locale.getDefault()
                            ).format(Date(article.date))
                            val annotatedString = buildAnnotatedString {
                                append("Summarized on: ")
                                pushStyle(SpanStyle(color = MaterialTheme.colorScheme.tertiary))
                                append(formattedDate)
                                pop()
                            }
                            Text(
                                text = annotatedString,
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = article.articleUrl,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    textDecoration = TextDecoration.Underline
                                ),
                                modifier = Modifier
                                    .clickable {
                                        context.startActivity(intent)
                                    }
                                    .padding(bottom = 16.dp)
                            )
                            Text(
                                modifier = Modifier
                                    .clickable {
                                        gotoWebView(article.articleUrl)
                                        onDismiss()
                                    },
                                text = "Open in Webview",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
                item {
                    val summaryText = buildAnnotatedString {
                        append("Summary Type: ")
                        pushStyle(SpanStyle(color = MaterialTheme.colorScheme.tertiary))
                        append(selectedSummaryType.displayName)
                        pop()
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = summaryText,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                                .clickable { expanded = !expanded }
                        )
                        IconButton(
                            onClick = { expanded = !expanded },
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(4.dp)
                                )
                        ) {
                            Icon(
                                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp
                                else Icons.Filled.KeyboardArrowDown,
                                contentDescription = if (expanded) "Collapse" else "Expand"
                            )
                        }
                    }
                    DropdownMenu(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface),
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        SummaryType.entries.forEach { type ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedSummaryType = type
                                    expanded = false
                                    reSummaryFunction(type, summary.ogText)

                                },
                                text = {
                                    Text(
                                        text = type.displayName,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = {
                                    if (selectedSummaryType == type) {
                                        Icon(
                                            imageVector = Icons.Filled.Done,
                                            contentDescription = "Info"
                                        )
                                    }

                                },
                                contentPadding = PaddingValues(4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Box {
                        when (val state = reSummarizeState.value) {
                            ReSummariseArticleUIState.Initial -> {
                                Text(
                                    text = simpleMarkdownToAnnotatedString(summary.summaryText),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            ReSummariseArticleUIState.Loading -> {
                                LinearProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            is ReSummariseArticleUIState.Error -> ErrorUi()
                            is ReSummariseArticleUIState.Success -> {
                                Text(
                                    text = simpleMarkdownToAnnotatedString(state.summary),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }

                    }

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
                            imageVector = if (showOriginalText) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
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
                            deleteArticle(article.articleId)
                            onDismiss.invoke()
                        },
                        enabled = article.articleId >= 0
                    ) {
                        Text(
                            text = "Delete",
                            modifier = Modifier.padding(4.dp),
                            style = MaterialTheme.typography.labelLarge
                        )
                        if (article.favouriteArticles) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete"
                            )
                        } else {
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


sealed interface ArticlePassInformation {
    data class ArticleObject(val article: ArticleWithSummaryUiModel) : ArticlePassInformation
    data class ArticleId(val articleId: Int) : ArticlePassInformation
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewSummaryScreen() {

    val dummyReSummarizeState =
        remember { mutableStateOf<ReSummariseArticleUIState>(ReSummariseArticleUIState.Initial) }


    SummaryScreen(
        articleWithSummaries = articleSummariesDummyData,
        onDismiss = {},
        addToFavorites = { _, _ -> },
        deleteArticle = {},
        saveArticle = {},
        sheetState = SheetState(
            skipPartiallyExpanded = true,
            initialValue = SheetValue.Expanded,
            confirmValueChange = { true },
            skipHiddenState = true,
            density = Density(1f),
        ),
        gotoWebView = {},
        reSummaryFunction = { _, _ -> },
        reSummarizeState = dummyReSummarizeState
    )
}

// Dummy data for preview