package shmr.budgetly.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shmr.budgetly.domain.model.TransactionFilterType
import shmr.budgetly.domain.usecase.GetHistoryUseCase
import shmr.budgetly.domain.usecase.GetMainAccountUseCase
import shmr.budgetly.domain.util.Result
import shmr.budgetly.ui.navigation.Expenses
import shmr.budgetly.ui.navigation.History
import shmr.budgetly.ui.navigation.Incomes
import shmr.budgetly.ui.util.DatePickerDialogType
import shmr.budgetly.ui.util.formatCurrencySymbol
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject


/**
 * ViewModel для экрана "История".
 * Отвечает за:
 * 1. Загрузку истории транзакций за выбранный период через [GetHistoryUseCase].
 * 2. Фильтрацию по типу (доходы/расходы) на основе родительского маршрута.
 * 3. Обработку выбора дат в DatePicker'е.
 * 4. Управление состоянием UI ([HistoryUiState]).
 */

class HistoryViewModel @Inject constructor(
    private val getHistory: GetHistoryUseCase,
    private val getMainAccount: GetMainAccountUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState = _uiState.asStateFlow()

    private lateinit var filterType: TransactionFilterType

    /**
     * Инициализирует ViewModel с необходимыми аргументами навигации.
     * Этот метод должен быть вызван сразу после создания ViewModel.
     */
    fun init(navArgs: History) {
        // Если ViewModel уже инициализирована, ничего не делаем
        if (this::filterType.isInitialized) return

        this.filterType = when (navArgs.parentRoute) {
            Expenses::class.qualifiedName -> TransactionFilterType.EXPENSE
            Incomes::class.qualifiedName -> TransactionFilterType.INCOME
            else -> TransactionFilterType.ALL
        }
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
        if (!this::filterType.isInitialized) {
            return
        }

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

            val grouped = transactions
                .sortedByDescending { it.transactionDate }
                .groupBy { it.transactionDate.toLocalDate() }

            _uiState.update {
                it.copy(
                    transactionsByDate = grouped,
                    currencySymbol = currencySymbol,
                    isLoading = false
                )
            }
        }
    }
}