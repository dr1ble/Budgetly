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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import shmr.budgetly.R
import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.ui.components.BaseListItem
import shmr.budgetly.ui.components.DatePickerModal
import shmr.budgetly.ui.components.EmojiIcon
import shmr.budgetly.ui.theme.dimens
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        HistoryHeader(
            startDate = uiState.startDate,
            endDate = uiState.endDate,
            totalSum = uiState.totalSum,
            onStartDateClick = viewModel::onStartDatePickerOpen,
            onEndDateClick = viewModel::onEndDatePickerOpen
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                uiState.error != null -> Text(
                    uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> HistoryList(uiState.transactionsByDate)
            }
        }
    }
    if (uiState.openDialog != null) {
        val initialDate = when (uiState.openDialog) {
            DatePickerDialogType.START_DATE -> uiState.startDate
            DatePickerDialogType.END_DATE -> uiState.endDate
            null -> LocalDate.now()
        }

        DatePickerModal(
            selectedDate = initialDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
                .toEpochMilli(),
            onDateSelected = viewModel::onDateSelected,
            onDismiss = viewModel::onDatePickerDismiss
        )
    }
}

private fun formatTransactionDate(dateTime: LocalDateTime): String {
    val monthsInGenitiveCase = mapOf(
        1 to "января",
        2 to "февраля",
        3 to "марта",
        4 to "апреля",
        5 to "мая",
        6 to "июня",
        7 to "июля",
        8 to "августа",
        9 to "сентября",
        10 to "октября",
        11 to "ноября",
        12 to "декабря"
    )

    val day = dateTime.dayOfMonth
    val month = monthsInGenitiveCase[dateTime.monthValue] ?: ""
    val time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))

    return "$day $month $time"
}

private fun formatHeaderDate(date: LocalDate): String {
    val monthsInGenitiveCase = mapOf(
        1 to "января",
        2 to "февраля",
        3 to "марта",
        4 to "апреля",
        5 to "мая",
        6 to "июня",
        7 to "июля",
        8 to "августа",
        9 to "сентября",
        10 to "октября",
        11 to "ноября",
        12 to "декабря"
    )

    val monthName = if (date.monthValue == 5 || date.monthValue == 8) {
        date.month.getDisplayName(java.time.format.TextStyle.FULL, Locale("ru"))
    } else {
        monthsInGenitiveCase[date.monthValue] ?: ""
    }

    val day = date.dayOfMonth
    val year = date.year

    return "$day $monthName $year"
}


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
            title = "Начало",
            defaultHeight = MaterialTheme.dimens.heights.small,
            trail = {
                Text(text = formatHeaderDate(startDate))
            },
            onClick = onStartDateClick
        )
        BaseListItem(
            title = "Конец",
            defaultHeight = MaterialTheme.dimens.heights.small,
            trail = {
                Text(text = formatHeaderDate(endDate))
            },
            onClick = onEndDateClick
        )
        BaseListItem(
            title = "Сумма",
            defaultHeight = MaterialTheme.dimens.heights.small,
            trail = {
                Text(
                    text = totalSum
                )
            },
            showDivider = false
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HistoryList(transactionsByDate: Map<LocalDate, List<Transaction>>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        transactionsByDate.onEachIndexed { index, entry ->
            items(
                items = entry.value,
                key = { transaction -> transaction.id }
            ) { transaction ->
                TransactionItem(transaction = transaction)
            }
        }
    }
}


@Composable
private fun TransactionItem(transaction: Transaction) {

    BaseListItem(
        lead = { EmojiIcon(emoji = transaction.category.emoji) },
        title = transaction.category.name,
        subtitle = transaction.comment?.takeIf { it.isNotBlank() },
        trail = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = transaction.amount,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = formatTransactionDate(transaction.transactionDate),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                painterResource(R.drawable.ic_list_item_trail_arrow),
                contentDescription = ("")
            )
        }
    )
}