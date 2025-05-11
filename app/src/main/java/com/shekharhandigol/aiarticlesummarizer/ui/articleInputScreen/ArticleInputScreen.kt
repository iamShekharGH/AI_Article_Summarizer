package com.shekharhandigol.aiarticlesummarizer.ui.articleInputScreen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch


@Composable
fun MainArticleInputScreen() {
    val viewModel: ArticleInputScreenViewModel = hiltViewModel()
    val screenState = viewModel.summaryText.collectAsStateWithLifecycle()

    ArticleInputScreen(
        onSummarize = { viewModel.summarizeText(it) },
        saveArticleToDb = { url, summary, title ->
            viewModel.saveArticleToDb(url, title, summary)
        },
        screenState.value
    )


}

@Composable
fun ArticleInputScreen(
    onSummarize: (String) -> Unit,
    saveArticleToDb: (String, String, String) -> Unit = { _, _, _ -> },
    screenStateValue: ArticleInputScreenUIState,
    ) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showSaveDialog by remember { mutableStateOf(false) }
    var url by remember { mutableStateOf("") }



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
                    IconButton(onClick = { url = "" }) {
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
            Button(
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
                enabled = url.isNotBlank()
            ) {
                Text("Summarize")
            }
            Text(
                text = when (screenStateValue) {
                    is ArticleInputScreenUIState.Error -> screenStateValue.text
                    is ArticleInputScreenUIState.Initial -> screenStateValue.text
                    ArticleInputScreenUIState.Loading -> "Loading..."
                    is ArticleInputScreenUIState.Success -> {
                        showSaveDialog = true
                        screenStateValue.title + "\n" + screenStateValue.description
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            )
        }

        if (showSaveDialog) {
            AlertDialog(
                onDismissRequest = { showSaveDialog = false },
                title = { Text("Save Article?") },
                text = { Text("Do you want to save this summarized article?") },
                confirmButton = {
                    Button(onClick = {
                        if (screenStateValue is ArticleInputScreenUIState.Success) {
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
                    Button(onClick = { showSaveDialog = false }) {
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
        onSummarize = { /* Dummy summarize action */ },
        saveArticleToDb = { _, _, _ -> /* Dummy save action */ },
        screenStateValue = ArticleInputScreenUIState.Initial("Your Results will show up here.")
    )
}