package shmr.budgetly.ui.screens.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.model.TransactionFilterType
import shmr.budgetly.domain.usecase.GetHistoryUseCase
import shmr.budgetly.domain.util.DomainError
import shmr.budgetly.domain.util.Result
import shmr.budgetly.ui.navigation.NavDestination
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

enum class DatePickerDialogType {
    START_DATE, END_DATE
}

data class HistoryUiState(
    val transactionsByDate: Map<LocalDate, List<Transaction>> = emptyMap(),
    val startDate: LocalDate = LocalDate.now().withDayOfMonth(1),
    val endDate: LocalDate = LocalDate.now(),
    val totalSum: String = "0 ₽",
    val isLoading: Boolean = false,
    val openDialog: DatePickerDialogType? = null,
    val error: DomainError? = null
)

/**
 * ViewModel для экрана "История".
 * Отвечает за загрузку истории транзакций за выбранный период через GetHistoryUseCase,
 * фильтрацию по типу (доходы/расходы) и обработку выбора дат.
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getHistory: GetHistoryUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState = _uiState.asStateFlow()

    private val parentRoute: String? = savedStateHandle[NavDestination.History.PARENT_ROUTE_ARG]

    init {
        loadHistory(isInitialLoad = true)
    }

    fun onStartDatePickerOpen() =
        _uiState.update { it.copy(openDialog = DatePickerDialogType.START_DATE) }

    fun onEndDatePickerOpen() =
        _uiState.update { it.copy(openDialog = DatePickerDialogType.END_DATE) }

    fun onDatePickerDismiss() = _uiState.update { it.copy(openDialog = null) }

    fun onDateSelected(dateInMillis: Long?) {
        if (dateInMillis == null) {
            onDatePickerDismiss()
            return
        }

        val newDate = Instant.ofEpochMilli(dateInMillis).atZone(ZoneId.of("UTC")).toLocalDate()
        val currentDialog = _uiState.value.openDialog

        _uiState.update { currentState ->
            when (currentDialog) {
                DatePickerDialogType.START_DATE -> if (newDate.isAfter(currentState.endDate)) currentState else currentState.copy(
                    startDate = newDate
                )

                DatePickerDialogType.END_DATE -> if (newDate.isBefore(currentState.startDate)) currentState else currentState.copy(
                    endDate = newDate
                )
                null -> currentState
            }
        }

        onDatePickerDismiss()
        // Если дата действительно изменилась, инициируем перезагрузку
        if (currentDialog != null) {
            loadHistory(isInitialLoad = true)
        }
    }

    fun loadHistory(isInitialLoad: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = isInitialLoad, error = null) }

            val filterType = when (parentRoute) {
                NavDestination.BottomNav.Expenses.route -> TransactionFilterType.EXPENSE
                NavDestination.BottomNav.Incomes.route -> TransactionFilterType.INCOME
                else -> TransactionFilterType.ALL
            }

            val result = getHistory(
                startDate = _uiState.value.startDate,
                endDate = _uiState.value.endDate,
                filterType = filterType
            )
            processResult(result)
        }
    }

    private fun processResult(result: Result<List<Transaction>>) {
        when (result) {
            is Result.Success -> {
                val total = result.data.sumOf {
                    it.amount.replace(Regex("[^0-9.-]"), "").toDoubleOrNull() ?: 0.0
                }
                val grouped = result.data
                    .sortedByDescending { it.transactionDate }
                    .groupBy { it.transactionDate.toLocalDate() }

                _uiState.update {
                    it.copy(
                        transactionsByDate = grouped,
                        totalSum = "%,.0f ₽".format(total).replace(",", " "),
                        isLoading = false
                    )
                }
            }

            is Result.Error -> {
                _uiState.update { it.copy(error = result.error, isLoading = false) }
            }
        }
    }
}