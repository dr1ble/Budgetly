package shmr.budgetly.ui.screens.articles

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import shmr.budgetly.ui.components.AppSearchBar
import shmr.budgetly.ui.components.BaseListItem
import shmr.budgetly.ui.components.EmojiIcon

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArticlesScreen(
    modifier: Modifier = Modifier,
    viewModel: ArticlesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
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