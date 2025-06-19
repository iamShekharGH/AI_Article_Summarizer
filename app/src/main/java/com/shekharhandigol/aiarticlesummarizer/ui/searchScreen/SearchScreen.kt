package com.shekharhandigol.aiarticlesummarizer.ui.searchScreen


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
    val tags by viewModel.tags.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getAllTags()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
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
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow {
                items(tags.size) { tag ->
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = tags[tag],
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    viewModel.getArticlesByTag(tags[tag])
                                }
                        )
                    }
                    /*Text(
                        text = tags[tag],
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(4.dp)
                            .clickable {
                                viewModel.onQueryChange(tags[tag])
                            }
                    )*/
                }
            }
            Text(
                text = "Search Results",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
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
                    LazyColumn {
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


@Preview
@Composable
fun PreviewLocalSearchScreen() {
    LocalSearchScreen(onArticleClick = {})
}