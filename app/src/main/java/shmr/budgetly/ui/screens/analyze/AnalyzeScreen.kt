package shmr.budgetly.ui.screens.analyze

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import shmr.budgetly.R
import shmr.budgetly.domain.model.AnalysisItem
import shmr.budgetly.ui.components.AmountText
import shmr.budgetly.ui.components.AppTopBar
import shmr.budgetly.ui.components.BaseListItem
import shmr.budgetly.ui.components.DatePickerModal
import shmr.budgetly.ui.components.EmojiIcon
import shmr.budgetly.ui.components.ErrorState
import shmr.budgetly.ui.theme.dimens
import shmr.budgetly.ui.util.HistoryDateFormatter
import shmr.budgetly.ui.util.LocalTopAppBarSetter
import shmr.budgetly.ui.util.formatAmount
import shmr.budgetly.ui.util.getErrorMessage
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

@Composable
fun AnalyzeScreen(
    viewModel: AnalyzeViewModel,
    navController: NavController,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val topAppBarSetter = LocalTopAppBarSetter.current

    LaunchedEffect(uiState.title) {
        topAppBarSetter {
            AppTopBar(
                title = uiState.title,
                navigationIcon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) },
                onNavigationClick = { navController.popBackStack() }
            )
        }
    }

    if (uiState.isDatePickerVisible) {
        DatePickerModal(
            selectedDate = (if (uiState.datePickerType == shmr.budgetly.ui.util.DatePickerDialogType.START_DATE) uiState.startDate else uiState.endDate)
                .atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli(),
            onDateSelected = viewModel::onDateSelected,
            onDismiss = viewModel::onDatePickerDismiss
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator()
            }

            uiState.error != null -> {
                ErrorState(
                    message = getErrorMessage(error = uiState.error!!),
                    onRetry = viewModel::onRetry
                )
            }

            else -> {
                AnalysisContent(
                    uiState = uiState,
                    onStartDateClick = viewModel::onStartDatePickerOpen,
                    onEndDateClick = viewModel::onEndDatePickerOpen,
                )
            }
        }
    }
}


@Composable
private fun AnalysisContent(
    uiState: AnalyzeUiState,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit
) {
    val analysisResult = uiState.analysisResult

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            Header(
                startDate = uiState.startDate,
                endDate = uiState.endDate,
                totalAmount = analysisResult?.let {
                    formatAmount(it.totalAmount, it.currencySymbol)
                } ?: formatAmount(BigDecimal.ZERO, ""),
                onStartDateClick = onStartDateClick,
                onEndDateClick = onEndDateClick,
            )
        }

        if (analysisResult != null) {
            items(
                items = analysisResult.items,
                key = { it.category.id }
            ) { item ->
                AnalysisListItem(item = item, currencySymbol = analysisResult.currencySymbol)
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.analyze_no_data),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun Header(
    startDate: LocalDate,
    endDate: LocalDate,
    totalAmount: String,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit
) {
    Column {
        BaseListItem(
            title = "Период: начало",
            defaultHeight = MaterialTheme.dimens.heights.small,
            trail = { DateChip(text = HistoryDateFormatter.formatHeaderDate(startDate), onClick = onStartDateClick) }
        )
        BaseListItem(
            title = "Период: конец",
            defaultHeight = MaterialTheme.dimens.heights.small,
            trail = { DateChip(text = HistoryDateFormatter.formatHeaderDate(endDate), onClick = onEndDateClick) }
        )
        BaseListItem(
            title = "Сумма",
            defaultHeight = MaterialTheme.dimens.heights.small,
            trail = {
                Text(
                    text = totalAmount,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        )
    }
}

@Composable
fun DateChip(text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clip(CircleShape),
        color = MaterialTheme.colorScheme.primary
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun AnalysisListItem(
    item: AnalysisItem,
    currencySymbol: String
) {
    val percentageString = String.format(Locale.US, "%.1f%%", item.percentage)
    val amountString = formatAmount(item.totalAmount, currencySymbol)

    BaseListItem(
        lead = { EmojiIcon(emoji = item.category.emoji) },
        title = item.category.name,
        subtitle = item.exampleComment.ifBlank { null },
        truncateSubtitle = true,
        trail = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = percentageString,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    AmountText(text = amountString)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_list_item_trail_arrow),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}