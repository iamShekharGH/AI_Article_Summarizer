package com.shekharhandigol.aiarticlesummarizer.ui.articleInputScreen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shekharhandigol.aiarticlesummarizer.SharedUrl
import com.shekharhandigol.aiarticlesummarizer.database.Article
import com.shekharhandigol.aiarticlesummarizer.database.ArticleWithSummaries
import com.shekharhandigol.aiarticlesummarizer.database.Summary
import kotlinx.coroutines.launch


@Composable
fun MainArticleInputScreen(
    onArticleClick: (Int) -> Unit,
    showJustSummarizedText: (ArticleWithSummaries) -> Unit,
    sharedUrl: SharedUrl
) {
    val viewModel: ArticleInputScreenViewModel = hiltViewModel()
    val screenState = viewModel.summaryText.collectAsStateWithLifecycle()

    ArticleInputScreen(
        onSummarize = { viewModel.summarizeText(it) },
        saveArticleToDb = { url, summary, title ->
            viewModel.saveArticleToDb(url, title, summary)
        },
        screenStateValue = screenState.value,
        onArticleClick = onArticleClick,
        showJustSummarizedText = showJustSummarizedText,
        sharedUrl = sharedUrl,
        resetState = { viewModel.resetToInitial() }
    )


}

@Composable
fun ArticleInputScreen(
    onSummarize: (String) -> Unit,
    saveArticleToDb: (String, String, String) -> Unit,
    screenStateValue: ArticleInputScreenUIState,
    onArticleClick: (Int) -> Unit,
    showJustSummarizedText: (ArticleWithSummaries) -> Unit,
    sharedUrl: SharedUrl = SharedUrl.None,
    resetState: () -> Unit
    ) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showSaveDialog by remember { mutableStateOf(false) }
    var url by remember { mutableStateOf("") }

    LaunchedEffect(sharedUrl) {
        if (sharedUrl is SharedUrl.Url) {
            url = sharedUrl.url
        }
    }


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("Enter Article URL") },
                modifier = Modifier
                    .fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = {
                        url = ""
                        resetState()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "clear text"
                        )
                    }
                },
                prefix = {
                    Text("URL://")
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = {
                    onSummarize(url)
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Summarizing...",
                            duration = SnackbarDuration.Short,
                            withDismissAction = true,
                            actionLabel = "Dismiss",
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = url.isNotBlank(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Summarize")
            }
            var resultText = ""
            when (screenStateValue) {
                is ArticleInputScreenUIState.Error -> {
                    resultText = screenStateValue.text
                }

                is ArticleInputScreenUIState.Initial -> {
                    resultText = screenStateValue.text
                }

                ArticleInputScreenUIState.Loading -> {
                    resultText = "Loading..."
                }

                is ArticleInputScreenUIState.SavedToDbSuccessfully -> {
                    LaunchedEffect(Unit) {
                        onArticleClick(screenStateValue.id.toInt())
                    }
                }

                is ArticleInputScreenUIState.UrlSummarisedSuccessfully -> {
                    LaunchedEffect(Unit) {
                        showSaveDialog = true
                    }

                    resultText = screenStateValue.title + "\n" + screenStateValue.description
                }
            }
            Text(
                text = resultText,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }

        if (showSaveDialog) {
            AlertDialog(
                onDismissRequest = { showSaveDialog = false },
                title = { Text("Save Article?") },
                text = { Text("Do you want to save this summarized article?") },
                confirmButton = {
                    Button(onClick = {
                        if (screenStateValue is ArticleInputScreenUIState.UrlSummarisedSuccessfully) {
                            saveArticleToDb(
                                url,
                                screenStateValue.title,
                                screenStateValue.description
                            )
                        }

                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Article saved!",
                                duration = SnackbarDuration.Short
                            )
                        }
                        showSaveDialog = false
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showJustSummarizedText(
                            ArticleWithSummaries(
                                article = Article(
                                    title = (screenStateValue as ArticleInputScreenUIState.UrlSummarisedSuccessfully).title,
                                    articleUrl = url,
                                    favouriteArticles = false
                                ),
                                summaries = listOf(
                                    Summary(
                                        summaryText = screenStateValue.description,
                                        summaryId = -1,
                                        articleId = -1,
                                    )
                                )
                            )
                        )
                        showSaveDialog = false
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }

        }
    }


@Preview(showBackground = true)
@Composable
fun PreviewArticleInputScreen() {
    ArticleInputScreen(
        onSummarize = { },
        saveArticleToDb = { _, _, _ -> },
        screenStateValue = ArticleInputScreenUIState.Initial("Your Results will show up here."),
        onArticleClick = {},
        showJustSummarizedText = {},
        resetState = {}
    )
}