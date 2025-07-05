package shmr.budgetly.ui.screens.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.model.TransactionFilterType
import shmr.budgetly.domain.usecase.GetHistoryUseCase
import shmr.budgetly.domain.usecase.GetMainAccountUseCase
import shmr.budgetly.domain.util.DomainError
import shmr.budgetly.domain.util.Result
import shmr.budgetly.ui.navigation.NavDestination
import shmr.budgetly.ui.util.formatCurrencySymbol
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

enum class DatePickerDialogType { START_DATE, END_DATE }

data class HistoryUiState(
    val transactionsByDate: Map<LocalDate, List<Transaction>> = emptyMap(),
    val startDate: LocalDate = LocalDate.now().withDayOfMonth(1),
    val endDate: LocalDate = LocalDate.now(),
    val totalSum: String = "0",
    val isLoading: Boolean = false,
    val datePickerType: DatePickerDialogType? = null,
    val error: DomainError? = null
) {
    val isDatePickerVisible: Boolean get() = datePickerType != null
}

/**
 * ViewModel для экрана "История".
 * Отвечает за:
 * 1. Загрузку истории транзакций за выбранный период через [GetHistoryUseCase].
 * 2. Фильтрацию по типу (доходы/расходы) на основе родительского маршрута.
 * 3. Обработку выбора дат в DatePicker'е.
 * 4. Управление состоянием UI ([HistoryUiState]).
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getHistory: GetHistoryUseCase,
    private val getMainAccount: GetMainAccountUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState = _uiState.asStateFlow()

    private val parentRoute: String? = savedStateHandle[NavDestination.History.PARENT_ROUTE_ARG]
    private val filterType: TransactionFilterType = when (parentRoute) {
        NavDestination.BottomNav.Expenses.route -> TransactionFilterType.EXPENSE
        NavDestination.BottomNav.Incomes.route -> TransactionFilterType.INCOME
        else -> TransactionFilterType.ALL
    }

    init {
        loadHistory(isInitialLoad = true)
    }

    fun onStartDatePickerOpen() =
        _uiState.update { it.copy(datePickerType = DatePickerDialogType.START_DATE) }

    fun onEndDatePickerOpen() =
        _uiState.update { it.copy(datePickerType = DatePickerDialogType.END_DATE) }

    fun onDatePickerDismiss() = _uiState.update { it.copy(datePickerType = null) }

    fun onDateSelected(dateInMillis: Long?) {
        val selectedDate = dateInMillis?.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate()
        } ?: return onDatePickerDismiss()

        val currentDialog = _uiState.value.datePickerType
        var dateChanged = false

        _uiState.update { state ->
            when (currentDialog) {
                DatePickerDialogType.START_DATE -> if (!selectedDate.isAfter(state.endDate)) {
                    dateChanged = true
                    state.copy(startDate = selectedDate)
                } else state

                DatePickerDialogType.END_DATE -> if (!selectedDate.isBefore(state.startDate)) {
                    dateChanged = true
                    state.copy(endDate = selectedDate)
                } else state

                null -> state
            }
        }

        onDatePickerDismiss()
        if (dateChanged) {
            loadHistory(isInitialLoad = true)
        }
    }

    fun loadHistory(isInitialLoad: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = isInitialLoad, error = null) }

            val accountResultDeferred = async { getMainAccount() }
            val historyResultDeferred = async {
                getHistory(
                    startDate = _uiState.value.startDate,
                    endDate = _uiState.value.endDate,
                    filterType = filterType
                )
            }

            val accountResult = accountResultDeferred.await()
            val historyResult = historyResultDeferred.await()

            val error = (accountResult as? Result.Error)?.error
                ?: (historyResult as? Result.Error)?.error

            if (error != null) {
                _uiState.update { it.copy(isLoading = false, error = error) }
                return@launch
            }

            val account = (accountResult as Result.Success).data
            val transactions = (historyResult as Result.Success).data

            val currencySymbol = formatCurrencySymbol(account.currency)
            val total = transactions.sumOf {
                it.amount.replace(Regex("[^0-9.-]"), "").toDoubleOrNull() ?: 0.0
            }

            val grouped = transactions
                .sortedByDescending { it.transactionDate }
                .groupBy { it.transactionDate.toLocalDate() }

            _uiState.update {
                it.copy(
                    transactionsByDate = grouped,
                    totalSum = "%,.0f %s".format(total, currencySymbol).replace(",", " "),
                    isLoading = false
                )
            }
        }
    }
}