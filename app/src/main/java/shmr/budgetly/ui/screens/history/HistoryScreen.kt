package shmr.budgetly.ui.screens.history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import shmr.budgetly.R
import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.util.DomainError
import shmr.budgetly.ui.components.AppTopBar
import shmr.budgetly.ui.components.BaseListItem
import shmr.budgetly.ui.components.DatePickerModal
import shmr.budgetly.ui.components.EmojiIcon
import shmr.budgetly.ui.components.ErrorState
import shmr.budgetly.ui.navigation.Analyze
import shmr.budgetly.ui.navigation.TRANSACTION_SAVED_RESULT_KEY
import shmr.budgetly.ui.navigation.TransactionDetails
import shmr.budgetly.ui.theme.dimens
import shmr.budgetly.ui.util.DatePickerDialogType
import shmr.budgetly.ui.util.HistoryDateFormatter
import shmr.budgetly.ui.util.LocalTopAppBarSetter
import shmr.budgetly.ui.util.formatCurrencySymbol
import java.time.LocalDate
import java.time.ZoneId

/**
 * Экран "История", отображающий список транзакций за выбранный период.
 * Позволяет пользователю выбирать начальную и конечную даты.
 */
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val topAppBarSetter = LocalTopAppBarSetter.current

    LaunchedEffect(uiState.parentRoute) {
        topAppBarSetter {
            AppTopBar(
                title = stringResource(R.string.history_top_bar_title),
                navigationIcon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) },
                onNavigationClick = { navController.popBackStack() },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(
                            Analyze(
                                parentRoute = uiState.parentRoute,
                                startDate = uiState.startDate.toEpochDay(),
                                endDate = uiState.endDate.toEpochDay()
                            )
                        )
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_history_analyze),
                            contentDescription = stringResource(R.string.analyze_action_description),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    }


    val navBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(navBackStackEntry) {
        if (navBackStackEntry?.savedStateHandle?.remove<Boolean>(TRANSACTION_SAVED_RESULT_KEY) == true) {
            viewModel.loadHistory(isInitialLoad = true)
        }
    }


    if (uiState.isDatePickerVisible) {
        DatePickerDialog(uiState, viewModel)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        HistoryHeader(
            startDate = uiState.startDate,
            endDate = uiState.endDate,
            totalSum = uiState.totalSum,
            onStartDateClick = viewModel::onStartDatePickerOpen,
            onEndDateClick = viewModel::onEndDatePickerOpen
        )
        HistoryContent(
            uiState = uiState,
            onRetry = { viewModel.loadHistory(isInitialLoad = true) },
            onTransactionClick = { transaction ->
                navController.navigate(
                    TransactionDetails(
                        transactionId = transaction.id,
                        isIncome = transaction.category.isIncome,
                        parentRoute = uiState.parentRoute
                    )
                )
            }
        )
    }
}

/**
 * Отображает контент экрана: индикатор загрузки, ошибку или список транзакций.
 */
@Composable
private fun HistoryContent(
    uiState: HistoryUiState,
    onRetry: () -> Unit,
    onTransactionClick: (Transaction) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when {
            uiState.isLoading -> CircularProgressIndicator()
            uiState.error != null -> ErrorContent(uiState.error, onRetry)
            else -> HistoryList(uiState.transactionsByDate, onTransactionClick)
        }
    }
}

/**
 * Отображает модальное окно выбора даты.
 */
@Composable
private fun DatePickerDialog(uiState: HistoryUiState, viewModel: HistoryViewModel) {
    val datePickerType = uiState.datePickerType ?: return

    val initialDate = when (datePickerType) {
        DatePickerDialogType.START_DATE -> uiState.startDate
        DatePickerDialogType.END_DATE -> uiState.endDate
    }
    DatePickerModal(
        selectedDate = initialDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli(),
        onDateSelected = viewModel::onDateSelected,
        onDismiss = viewModel::onDatePickerDismiss
    )
}

/**
 * Отображает сообщение об ошибке с кнопкой "Повторить".
 */
@Composable
private fun ErrorContent(error: DomainError, onRetry: () -> Unit) {
    val errorMessage = when (error) {
        DomainError.NoInternet -> stringResource(R.string.error_no_internet)
        DomainError.ServerError -> stringResource(R.string.error_server)
        is DomainError.Unknown -> stringResource(R.string.error_unknown)
    }
    ErrorState(message = errorMessage, onRetry = onRetry)
}

/**
 * Отображает заголовок с выбором периода и общей суммой.
 */
@Composable
private fun HistoryHeader(
    startDate: LocalDate,
    endDate: LocalDate,
    totalSum: String,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit
) {
    Column(modifier = Modifier.background(MaterialTheme.colorScheme.secondary)) {
        BaseListItem(
            title = stringResource(R.string.history_start_date),
            defaultHeight = MaterialTheme.dimens.heights.small,
            trail = { Text(text = HistoryDateFormatter.formatHeaderDate(startDate)) },
            onClick = onStartDateClick
        )
        BaseListItem(
            title = stringResource(R.string.history_end_date),
            defaultHeight = MaterialTheme.dimens.heights.small,
            trail = { Text(text = HistoryDateFormatter.formatHeaderDate(endDate)) },
            onClick = onEndDateClick
        )
        BaseListItem(
            title = stringResource(R.string.history_total_sum),
            defaultHeight = MaterialTheme.dimens.heights.small,
            trail = { Text(text = totalSum) },
            showDivider = false
        )
    }
}

/**
 * Отображает список транзакций, сгруппированных по дате.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HistoryList(
    transactionsByDate: Map<LocalDate, List<Transaction>>,
    onTransactionClick: (Transaction) -> Unit
) {
    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        transactionsByDate.forEach { (_, transactions) ->
            items(items = transactions, key = { it.id }) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    onClick = { onTransactionClick(transaction) }
                )
            }
        }
    }
}

/**
 * Отображает один элемент списка транзакций.
 */
@Composable
private fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    BaseListItem(
        lead = { EmojiIcon(emoji = transaction.category.emoji) },
        title = transaction.category.name,
        subtitle = transaction.comment.takeIf { it.isNotBlank() },
        onClick = onClick,
        trail = {
            val currencySymbol = formatCurrencySymbol(transaction.currency)
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "${transaction.amount} $currencySymbol",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = HistoryDateFormatter.formatTransactionDate(transaction.transactionDate),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                painterResource(R.drawable.ic_list_item_trail_arrow),
                contentDescription = null // Декоративный элемент
            )
        }
    )
}