package shmr.budgetly.ui.screens.expenses

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import shmr.budgetly.R
import shmr.budgetly.ui.components.AmountText
import shmr.budgetly.ui.components.AppTopBar
import shmr.budgetly.ui.components.BaseListItem
import shmr.budgetly.ui.components.EmojiIcon
import shmr.budgetly.ui.components.ErrorState
import shmr.budgetly.ui.components.TotalHeader
import shmr.budgetly.ui.navigation.Expenses
import shmr.budgetly.ui.navigation.History
import shmr.budgetly.ui.navigation.TRANSACTION_SAVED_RESULT_KEY
import shmr.budgetly.ui.navigation.TransactionDetails
import shmr.budgetly.ui.util.LocalTopAppBarSetter
import shmr.budgetly.ui.util.formatAmount
import shmr.budgetly.ui.util.formatCurrencySymbol
import shmr.budgetly.ui.util.getErrorMessage

/**
 * Экран "Расходы". Отображает список транзакций-расходов пользователя за текущий месяц.
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun ExpensesScreen(
    modifier: Modifier = Modifier,
    viewModel: ExpensesViewModel,
    navController: NavController
) {
    val topAppBarSetter = LocalTopAppBarSetter.current
    LaunchedEffect(Unit) {
        topAppBarSetter {
            AppTopBar(
                title = stringResource(R.string.expenses_top_bar_title),
                actions = {
                    IconButton(onClick = { navController.navigate(History(parentRoute = Expenses::class.qualifiedName!!)) }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_top_bar_history),
                            contentDescription = stringResource(R.string.history_action_description),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(navBackStackEntry) {
        if (navBackStackEntry?.savedStateHandle?.remove<Boolean>(TRANSACTION_SAVED_RESULT_KEY) == true) {
            viewModel.loadExpenses(isInitialLoad = true)
        }
    }


    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshing,
        onRefresh = { viewModel.loadExpenses(forceRefresh = true) }
    )

    Box(modifier = modifier
        .fillMaxSize()
        .pullRefresh(pullRefreshState)) {
        ScreenContent(
            uiState = uiState,
            onRetry = { viewModel.loadExpenses(isInitialLoad = true) },
            onTransactionClick = { transactionId ->
                navController.navigate(
                    TransactionDetails(
                        transactionId = transactionId,
                        isIncome = false,
                        parentRoute = Expenses::class.qualifiedName!!
                    )
                )
            }
        )

        PullRefreshIndicator(
            refreshing = uiState.isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BoxScope.ScreenContent(
    uiState: ExpensesUiState,
    onRetry: () -> Unit,
    onTransactionClick: (Int) -> Unit
) {
    when {
        uiState.isLoading && uiState.transactions.isEmpty() -> {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        uiState.error != null && uiState.transactions.isEmpty() -> {
            ErrorState(
                message = getErrorMessage(error = uiState.error),
                onRetry = onRetry
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
                        truncateSubtitle = true,
                        title = transaction.category.name,
                        subtitle = transaction.comment.ifBlank { null },
                        trail = {
                            val currencySymbol = formatCurrencySymbol(transaction.currency)
                            AmountText(
                                text = formatAmount(transaction.amount, currencySymbol)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Icon(
                                painterResource(R.drawable.ic_list_item_trail_arrow),
                                contentDescription = ("")
                            )
                        },
                        onClick = { onTransactionClick(transaction.id) }
                    )
                }
            }
        }
    }
}