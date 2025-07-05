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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import shmr.budgetly.R
import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.util.DomainError
import shmr.budgetly.ui.components.BaseListItem
import shmr.budgetly.ui.components.DatePickerModal
import shmr.budgetly.ui.components.EmojiIcon
import shmr.budgetly.ui.components.ErrorState
import shmr.budgetly.ui.theme.dimens
import shmr.budgetly.ui.util.HistoryDateFormatter
import shmr.budgetly.ui.util.formatCurrencySymbol
import java.time.LocalDate
import java.time.ZoneId

/**
 * Экран "История", отображающий список транзакций за выбранный период.
 * Позволяет пользователю выбирать начальную и конечную даты.
 *
 * @param viewModel ViewModel для управления состоянием экрана.
 */
@Composable
fun HistoryScreen(viewModel: HistoryViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
        HistoryContent(uiState, onRetry = { viewModel.loadHistory(isInitialLoad = true) })
    }
}

/**
 * Отображает контент экрана: индикатор загрузки, ошибку или список транзакций.
 */
@Composable
private fun HistoryContent(uiState: HistoryUiState, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when {
            uiState.isLoading -> CircularProgressIndicator()
            uiState.error != null -> ErrorContent(uiState.error, onRetry)
            else -> HistoryList(uiState.transactionsByDate)
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
private fun HistoryList(transactionsByDate: Map<LocalDate, List<Transaction>>) {
    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        transactionsByDate.forEach { (_, transactions) ->
            items(items = transactions, key = { it.id }) { transaction ->
                TransactionItem(transaction = transaction)
            }
        }
    }
}

/**
 * Отображает один элемент списка транзакций.
 */
@Composable
private fun TransactionItem(transaction: Transaction) {
    BaseListItem(
        lead = { EmojiIcon(emoji = transaction.category.emoji) },
        title = transaction.category.name,
        subtitle = transaction.comment.takeIf { it.isNotBlank() },
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