package shmr.budgetly.ui.screens.incomes

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import shmr.budgetly.R
import shmr.budgetly.ui.components.AppTopBar
import shmr.budgetly.ui.components.BaseListItem
import shmr.budgetly.ui.components.ErrorState
import shmr.budgetly.ui.components.TotalHeader
import shmr.budgetly.ui.navigation.History
import shmr.budgetly.ui.navigation.Incomes
import shmr.budgetly.ui.navigation.TRANSACTION_SAVED_RESULT_KEY
import shmr.budgetly.ui.navigation.TransactionDetails
import shmr.budgetly.ui.util.LocalTopAppBarSetter
import shmr.budgetly.ui.util.formatCurrencySymbol
import shmr.budgetly.ui.util.getErrorMessage

/**
 * Экран "Доходы". Отображает список транзакций-доходов пользователя за текущий месяц.
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun IncomesScreen(
    modifier: Modifier = Modifier,
    viewModel: IncomesViewModel,
    navController: NavController
) {
    val topAppBarSetter = LocalTopAppBarSetter.current
    LaunchedEffect(Unit) {
        topAppBarSetter {
            AppTopBar(
                title = stringResource(R.string.incomes_top_bar_title),
                actions = {
                    IconButton(onClick = { navController.navigate(History(parentRoute = Incomes::class.qualifiedName!!)) }) {
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
            viewModel.loadIncomes(isInitialLoad = true)
        }
    }


    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshing,
        onRefresh = { viewModel.loadIncomes() }
    )

    Box(modifier = modifier
        .fillMaxSize()
        .pullRefresh(pullRefreshState)) {
        ScreenContent(
            uiState = uiState,
            onRetry = { viewModel.loadIncomes(isInitialLoad = true) },
            onTransactionClick = { transactionId ->
                navController.navigate(
                    TransactionDetails(
                        transactionId = transactionId,
                        isIncome = true
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
    uiState: IncomesUiState,
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                stickyHeader {
                    TotalHeader(
                        totalAmount = uiState.totalAmount,
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                }
                items(
                    items = uiState.transactions,
                    key = { it.id }
                ) { transaction ->
                    BaseListItem(
                        title = transaction.category.name,
                        titleTextStyle = MaterialTheme.typography.bodyLarge,
                        trail = {
                            val currencySymbol = formatCurrencySymbol(transaction.currency)
                            Text(
                                text = "${transaction.amount} $currencySymbol",
                                fontWeight = FontWeight.Normal
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Icon(
                                painterResource(R.drawable.ic_list_item_trail_arrow),
                                contentDescription = ""
                            )
                        },
                        onClick = { onTransactionClick(transaction.id) }
                    )
                }
            }
        }
    }
}