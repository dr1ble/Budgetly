package shmr.budgetly.ui.screens.expenses

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import shmr.budgetly.R
import shmr.budgetly.ui.components.BaseListItem
import shmr.budgetly.ui.components.EmojiIcon
import shmr.budgetly.ui.components.TotalHeader
import shmr.budgetly.ui.theme.BudgetlyTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpensesScreen(
    modifier: Modifier = Modifier,
    viewModel: ExpensesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.error != null) {
            Text(
                text = uiState.error!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                stickyHeader {
                    TotalHeader(totalAmount = uiState.totalAmount)
                }
                items(
                    items = uiState.transactions,
                    key = { transaction -> transaction.id }
                ) { transaction ->
                    BaseListItem(
                        lead = {
                            EmojiIcon(
                                emoji = transaction.category.emoji,
                            )
                        },
                        title = transaction.category.name,
                        subtitle = if (transaction.comment.isNotBlank()) transaction.comment else null,
                        trail = {
                            Text(
                                text = transaction.amount,
                                fontWeight = FontWeight.Normal
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Icon(
                                painterResource(R.drawable.ic_list_item_trail_arrow),
                                contentDescription = ("")
                            )
                        },
                        onClick = { }
                    )
                }
            }
        }


        @Composable
        fun ExpensesScreenPreview() {
            BudgetlyTheme {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ExpensesScreen()
                }
            }
        }
    }
}