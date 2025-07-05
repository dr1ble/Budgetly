package shmr.budgetly.ui.screens.articles

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import shmr.budgetly.R
import shmr.budgetly.domain.util.DomainError
import shmr.budgetly.ui.components.AppSearchBar
import shmr.budgetly.ui.components.BaseListItem
import shmr.budgetly.ui.components.EmojiIcon
import shmr.budgetly.ui.components.ErrorState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArticlesScreen(
    modifier: Modifier = Modifier,
    viewModel: ArticlesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            uiState.error != null -> {
                val errorMessage = when (uiState.error) {
                    DomainError.NoInternet -> stringResource(R.string.error_no_internet)
                    DomainError.ServerError -> stringResource(R.string.error_server)
                    is DomainError.Unknown -> stringResource(R.string.error_unknown)
                    null -> ""
                }
                ErrorState(
                    message = errorMessage,
                    onRetry = { viewModel.loadCategories(isInitialLoad = true) }
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    stickyHeader {
                        Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                            AppSearchBar(
                                query = uiState.searchQuery,
                                onQueryChange = viewModel::onSearchQueryChanged,
                                placeholder = "Найти статью"
                            )
                        }
                    }
                    items(
                        items = uiState.filteredCategories,
                        key = { it.id }
                    ) { category ->
                        BaseListItem(
                            title = category.name,
                            titleTextStyle = MaterialTheme.typography.bodyLarge,
                            lead = { EmojiIcon(emoji = category.emoji) },
                            onClick = { }
                        )
                    }
                }
            }
        }
    }
}