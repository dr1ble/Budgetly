package shmr.budgetly.ui.screens.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shmr.budgetly.domain.entity.Account
import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.events.AppEvent
import shmr.budgetly.domain.events.AppEventBus
import shmr.budgetly.domain.usecase.GetExpenseTransactionsUseCase
import shmr.budgetly.domain.usecase.GetMainAccountUseCase
import shmr.budgetly.domain.usecase.RefreshTransactionsUseCase
import shmr.budgetly.domain.util.Result
import shmr.budgetly.ui.util.formatAmount
import shmr.budgetly.ui.util.formatCurrencySymbol
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

class ExpensesViewModel @Inject constructor(
    private val getExpenseTransactions: GetExpenseTransactionsUseCase,
    private val getMainAccount: GetMainAccountUseCase,
    private val refreshTransactions: RefreshTransactionsUseCase,
    private val appEventBus: AppEventBus
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpensesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadExpenses(isInitialLoad = true)
        observeAppEvents()
    }

    private fun observeAppEvents() {
        viewModelScope.launch {
            appEventBus.events.collect { event ->
                when (event) {
                    is AppEvent.AccountUpdated -> loadExpenses(forceRefresh = true)
                    is AppEvent.NetworkAvailable -> {
                        if (_uiState.value.error != null) {
                            loadExpenses(isInitialLoad = true)
                        }
                    }
                    is AppEvent.NavigateToPinSetup -> {
                    }
                    is AppEvent.PinStatusChanged -> {
                    }
                }
            }
        }
    }

    fun loadExpenses(isInitialLoad: Boolean = false, forceRefresh: Boolean = false) {
        val state = _uiState.value
        if (state.isLoading || state.isRefreshing) return

        val showLoading = isInitialLoad && state.transactions.isEmpty()
        val showRefreshing = forceRefresh || (isInitialLoad && !showLoading)

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = showLoading,
                    isRefreshing = showRefreshing,
                    error = null
                )
            }

            val today = LocalDate.now()
            refreshTransactions(today, today)

            val transactionsResult = getExpenseTransactions().first()
            val accountResult = getMainAccount().first()

            processResults(transactionsResult, accountResult)
        }
    }

    private fun processResults(
        transactionsResult: Result<List<Transaction>>,
        accountResult: Result<Account>
    ) {
        val transactions = (transactionsResult as? Result.Success)?.data
        val account = (accountResult as? Result.Success)?.data
        val error = (transactionsResult as? Result.Error)?.error
            ?: (accountResult as? Result.Error)?.error

        if (transactions != null && account != null) {
            val currencySymbol = formatCurrencySymbol(account.currency)
            val total = transactions.sumOf {
                try {
                    BigDecimal(it.amount.replace(',', '.'))
                } catch (_: NumberFormatException) {
                    BigDecimal.ZERO
                }
            }
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isRefreshing = false,
                    transactions = transactions,
                    totalAmount = formatAmount(total, currencySymbol),
                    error = null
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isRefreshing = false,
                    error = error
                )
            }
        }
    }
}