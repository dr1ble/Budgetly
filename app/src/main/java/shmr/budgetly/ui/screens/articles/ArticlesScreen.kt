package shmr.budgetly.ui.screens.articles

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import shmr.budgetly.data.MockData
import shmr.budgetly.ui.components.AppSearchBar
import shmr.budgetly.ui.components.BaseListItem
import shmr.budgetly.ui.components.EmojiIcon

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArticlesScreen(modifier: Modifier = Modifier) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredCategories = remember(searchQuery, MockData.allCategories) {
        if (searchQuery.isBlank()) {
            MockData.allCategories
        } else {
            MockData.allCategories.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        stickyHeader {
            Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                AppSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Найти статью"
                )
            }
        }


        items(
            items = filteredCategories,
            key = { it.id }
        ) { category ->
            BaseListItem(
                title = category.name,
                titleTextStyle = MaterialTheme.typography.bodyLarge,
                lead = {
                    EmojiIcon(
                        emoji = category.emoji
                    )
                },
                onClick = { }
            )
        }
    }
}