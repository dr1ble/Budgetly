package shmr.budgetly.ui.screens.incomes

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
import shmr.budgetly.domain.usecase.GetIncomeTransactionsUseCase
import shmr.budgetly.domain.usecase.GetMainAccountUseCase
import shmr.budgetly.domain.usecase.RefreshTransactionsUseCase
import shmr.budgetly.domain.util.Result
import shmr.budgetly.ui.util.formatAmount
import shmr.budgetly.ui.util.formatCurrencySymbol
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

class IncomesViewModel @Inject constructor(
    private val getIncomeTransactions: GetIncomeTransactionsUseCase,
    private val getMainAccount: GetMainAccountUseCase,
    private val refreshTransactions: RefreshTransactionsUseCase,
    private val appEventBus: AppEventBus
) : ViewModel() {

    private val _uiState = MutableStateFlow(IncomesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadIncomes(isInitialLoad = true)
        observeAppEvents()
    }

    private fun observeAppEvents() {
        viewModelScope.launch {
            appEventBus.events.collect { event ->
                when (event) {
                    is AppEvent.AccountUpdated -> loadIncomes(forceRefresh = true)
                    is AppEvent.NetworkAvailable -> {
                        if (_uiState.value.error != null) {
                            loadIncomes(isInitialLoad = true)
                        }
                    }
                }
            }
        }
    }

    fun loadIncomes(isInitialLoad: Boolean = false, forceRefresh: Boolean = false) {
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

            val transactionsResult = getIncomeTransactions().first()
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