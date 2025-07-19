package shmr.budgetly.ui.screens.articles

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import shmr.budgetly.R
import shmr.budgetly.ui.components.AppSearchBar
import shmr.budgetly.ui.components.AppTopBar
import shmr.budgetly.ui.components.BaseListItem
import shmr.budgetly.ui.components.EmojiIcon
import shmr.budgetly.ui.components.ErrorState
import shmr.budgetly.ui.util.LocalTopAppBarSetter
import shmr.budgetly.ui.util.getErrorMessage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArticlesScreen(
    modifier: Modifier = Modifier,
    viewModel: ArticlesViewModel,
    navController: NavController
) {
    val topAppBarSetter = LocalTopAppBarSetter.current
    LaunchedEffect(Unit) {
        topAppBarSetter {
            AppTopBar(title = stringResource(R.string.articles_top_bar_title))
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        ScreenContent(
            uiState = uiState,
            onSearchQueryChanged = viewModel::onSearchQueryChanged,
            onRetry = viewModel::onRetry
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BoxScope.ScreenContent(
    uiState: ArticlesUiState,
    onSearchQueryChanged: (String) -> Unit,
    onRetry: () -> Unit
) {
    when {
        uiState.isLoading && uiState.filteredCategories.isEmpty() -> {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        uiState.error != null && uiState.filteredCategories.isEmpty() -> {
            ErrorState(
                message = getErrorMessage(error = uiState.error),
                onRetry = onRetry
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
                            onQueryChange = onSearchQueryChanged,
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