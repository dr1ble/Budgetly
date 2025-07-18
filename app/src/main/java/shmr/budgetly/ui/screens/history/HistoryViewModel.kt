package shmr.budgetly.ui.screens.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shmr.budgetly.di.viewmodel.AssistedSavedStateViewModelFactory
import shmr.budgetly.domain.entity.Account
import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.model.TransactionFilterType
import shmr.budgetly.domain.usecase.GetHistoryUseCase
import shmr.budgetly.domain.usecase.GetMainAccountUseCase
import shmr.budgetly.domain.usecase.RefreshTransactionsUseCase
import shmr.budgetly.domain.util.Result
import shmr.budgetly.ui.navigation.Expenses
import shmr.budgetly.ui.navigation.History
import shmr.budgetly.ui.navigation.Incomes
import shmr.budgetly.ui.util.DatePickerDialogType
import shmr.budgetly.ui.util.formatCurrencySymbol
import java.time.Instant
import java.time.ZoneId

/**
 * ViewModel для экрана "История".
 * Отвечает за:
 * 1. Загрузку истории транзакций за выбранный период через [GetHistoryUseCase].
 * 2. Фильтрацию по типу (доходы/расходы) на основе родительского маршрута.
 * 3. Обработку выбора дат в DatePicker'е.
 * 4. Управление состоянием UI ([HistoryUiState]).
 */
class HistoryViewModel @AssistedInject constructor(
    private val getHistory: GetHistoryUseCase,
    private val getMainAccount: GetMainAccountUseCase,
    private val refreshTransactions: RefreshTransactionsUseCase,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState = _uiState.asStateFlow()

    private val filterType: TransactionFilterType

    init {
        val navArgs: History = savedStateHandle.toRoute()
        this.filterType = when (navArgs.parentRoute) {
            Expenses::class.qualifiedName -> TransactionFilterType.EXPENSE
            Incomes::class.qualifiedName -> TransactionFilterType.INCOME
            else -> TransactionFilterType.ALL
        }
        _uiState.update { it.copy(parentRoute = navArgs.parentRoute) }

        viewModelScope.launch {

            _uiState.flatMapLatest { state ->
                combine(
                    getHistory(state.startDate, state.endDate, filterType),
                    getMainAccount()
                ) { transactionsResult, accountResult ->
                    processResults(transactionsResult, accountResult)
                }
            }.collect {}
        }

        loadHistory(isInitialLoad = true)
    }

    @AssistedFactory
    interface Factory : AssistedSavedStateViewModelFactory<HistoryViewModel> {
        override fun create(savedStateHandle: SavedStateHandle): HistoryViewModel
    }

    private fun processResults(
        transactionsResult: Result<List<Transaction>>,
        accountResult: Result<Account>
    ) {
        val transactions = (transactionsResult as? Result.Success)?.data
        val account = (accountResult as? Result.Success)?.data
        val error = (transactionsResult as? Result.Error)?.error
            ?: (accountResult as? Result.Error)?.error

        if (error != null && _uiState.value.transactionsByDate.isEmpty()) {
            _uiState.update { it.copy(isLoading = false, isRefreshing = false, error = error) }
            return
        }

        if (transactions != null && account != null) {
            val currencySymbol = formatCurrencySymbol(account.currency)
            val grouped = transactions
                .sortedByDescending { it.transactionDate }
                .groupBy { it.transactionDate.toLocalDate() }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isRefreshing = false,
                    transactionsByDate = grouped,
                    currencySymbol = currencySymbol,
                    error = null
                )
            }
        }
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
            refreshTransactions(_uiState.value.startDate, _uiState.value.endDate)
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}