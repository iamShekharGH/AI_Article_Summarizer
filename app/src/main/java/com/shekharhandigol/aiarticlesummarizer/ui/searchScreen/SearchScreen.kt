package com.shekharhandigol.aiarticlesummarizer.ui.searchScreen


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shekharhandigol.aiarticlesummarizer.ui.articlesHome.ArticleListItem


@Composable
fun LocalSearchScreen(
    onArticleClick: (Int) -> Unit,
) {
    val viewModel: SearchScreenViewModel = hiltViewModel()
    val query by viewModel.query.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.onQueryChange(it) },
                label = {
                    Text(
                        text = "Search Articles",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(
                        onClick = { viewModel.onQueryChange("") }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Empty Search",
                        )
                    }

                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            when (val state = viewModel.uiState.collectAsStateWithLifecycle().value) {
                SearchScreenUiStates.Error -> {
                    Text(
                        text = "No articles found.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                SearchScreenUiStates.Loading -> {
                    Text(
                        text = "Loading...",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                SearchScreenUiStates.Initial -> {
                    Text(
                        text = "Search for articles",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                is SearchScreenUiStates.Success -> {
                    LazyColumn(modifier = Modifier.padding(8.dp)) {
                        items(state.articles.size) { article ->
                            ArticleListItem(
                                article = state.articles[article],
                                onArticleClick = onArticleClick,
                                onDeleteClick = viewModel::deleteArticle
                            )
                        }
                    }
                }
            }
        }
    }
}


