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
import androidx.compose.material3.MaterialTheme
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
import com.shekharhandigol.aiarticlesummarizer.core.ArticleWithSummaryUiModel
import kotlinx.coroutines.launch


@Composable
fun MainArticleInputScreen(
    onArticleClick: (Int) -> Unit,
    showJustSummarizedText: (ArticleWithSummaryUiModel) -> Unit
) {
    val viewModel: ArticleInputScreenViewModel = hiltViewModel()
    val screenState = viewModel.summaryText.collectAsStateWithLifecycle()

    ArticleInputScreen(
        onSummarize = { viewModel.summarizeText(it) },
        saveArticleToDb = { articleWithSummary ->
            viewModel.saveArticleToDb(articleWithSummary)
        },
        screenStateValue = screenState.value,
        onArticleClick = onArticleClick,
        showJustSummarizedText = showJustSummarizedText,
        resetState = { viewModel.resetToInitial() },
        getArticleWithSummaryObj = { viewModel.getArticleWithSummaryObj() }
    )


}

@Composable
fun ArticleInputScreen(
    onSummarize: (String) -> Unit,
    saveArticleToDb: (ArticleWithSummaryUiModel) -> Unit,
    screenStateValue: ArticleInputScreenUIState,
    onArticleClick: (Int) -> Unit,
    showJustSummarizedText: (ArticleWithSummaryUiModel) -> Unit,
    sharedUrl: SharedUrl = SharedUrl.None,
    resetState: () -> Unit,
    getArticleWithSummaryObj: () -> ArticleWithSummaryUiModel?
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showSaveDialog by remember { mutableStateOf(false) }
    var url by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
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
                label = {
                    Text(
                        text = "Enter Article URL",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
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
                    Text(
                        text = "URL://",
                        style = MaterialTheme.typography.bodyLarge
                    )
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
                Text(
                    text = "Summarize",
                    style = MaterialTheme.typography.labelLarge
                )
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

                    resultText =
                        "Title: ${screenStateValue.geminiJsoupResponseUiModel.title}\n" +
                                "\nSummary: ${screenStateValue.geminiJsoupResponseUiModel.onSummarise}"
                }
            }
            Text(
                text = resultText,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (showSaveDialog) {
            AlertDialog(
                onDismissRequest = { showSaveDialog = false },
                title = {
                    Text(
                        text = "Save Article?",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                text = {
                    Text(
                        text = "Do you want to save this summarized article?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        getArticleWithSummaryObj()?.let {
                            saveArticleToDb(it)
                        }
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Article saved!",
                                duration = SnackbarDuration.Short
                            )
                        }
                        showSaveDialog = false
                    }) {
                        Text(
                            text = "Save",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        getArticleWithSummaryObj()?.let {
                            showJustSummarizedText(it)
                        }
                        showSaveDialog = false
                    }) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.labelLarge
                        )
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
        saveArticleToDb = { },
        screenStateValue = ArticleInputScreenUIState.Initial("Your Results will show up here."),
        onArticleClick = {},
        showJustSummarizedText = {},
        resetState = {},
        getArticleWithSummaryObj = { null }
    )
}