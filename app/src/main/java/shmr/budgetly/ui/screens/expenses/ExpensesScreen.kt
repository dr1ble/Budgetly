package shmr.budgetly.ui.screens.expenses

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import shmr.budgetly.R
import shmr.budgetly.domain.util.DomainError
import shmr.budgetly.ui.components.BaseListItem
import shmr.budgetly.ui.components.EmojiIcon
import shmr.budgetly.ui.components.ErrorState
import shmr.budgetly.ui.components.TotalHeader
import shmr.budgetly.ui.util.formatCurrencySymbol


/*
* Экран "Расходы". Отображает список транзакций-расходов пользователя за текущий день.
*
*/
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun ExpensesScreen(
    modifier: Modifier = Modifier,
    viewModel: ExpensesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.loadExpenses(
                isInitialLoad = uiState.transactions.isEmpty(),
                forceRefresh = true
            )
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshing,
        onRefresh = { viewModel.loadExpenses(forceRefresh = true) }
    )

    Box(modifier = modifier
        .fillMaxSize()
        .pullRefresh(pullRefreshState)) {
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
                    onRetry = { viewModel.loadExpenses(isInitialLoad = true, forceRefresh = true) }
                )
            }

            else -> {
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
                                val currencySymbol = formatCurrencySymbol(transaction.currency)
                                Text(
                                    text = "${transaction.amount} $currencySymbol",
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
        }

        PullRefreshIndicator(
            refreshing = uiState.isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}