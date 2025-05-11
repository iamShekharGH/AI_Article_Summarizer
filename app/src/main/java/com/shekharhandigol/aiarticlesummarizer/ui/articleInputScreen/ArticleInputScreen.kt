package com.shekharhandigol.aiarticlesummarizer.ui.articleInputScreen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun MainArticleInputScreen() {
    val viewModel: ArticleInputScreenViewModel = hiltViewModel()
    val summaryText = viewModel.summaryText.collectAsState()

    ArticleInputScreen({ viewModel.summarizeText(it) }, summaryText)


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleInputScreen(
    onSummarize: (String) -> Unit,
    summaryText: State<String>,

    ) {
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp), // Adjust for keyboard
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Summarize Article",
                        textAlign = TextAlign.Center,
                    )
                },
                /*navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }*/
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
                    .fillMaxSize(),  // Make the column take up the whole screen
                verticalArrangement = Arrangement.Center, // Center content vertically
                horizontalAlignment = Alignment.CenterHorizontally // Center content horizontally
            ) {
                var url by remember { mutableStateOf("") }

                TextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Enter Article URL") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { url = "" }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "clear text"
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onSummarize(url) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = url.isNotBlank()
                ) {
                    Text("Summarize")
                }
                Text(
                    text = summaryText.value,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewArticleInputScreen() {
    ArticleInputScreen(
        onSummarize = { /* Dummy summarize action */ },
        summaryText = remember { mutableStateOf("") } // Dummy summary text state
    )
}