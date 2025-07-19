package shmr.budgetly.ui.screens.incomes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
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

/**
 * ViewModel для экрана "Доходы".
 * Отвечает за:
 * 1. Загрузку и подписку на обновления списка транзакций-доходов и данных счета.
 * 2. Управление состоянием UI ([IncomesUiState]).
 * 3. Реакцию на глобальные события, например, обновление счета.
 */
class IncomesViewModel @Inject constructor(
    private val getIncomeTransactions: GetIncomeTransactionsUseCase,
    private val getMainAccount: GetMainAccountUseCase,
    private val refreshTransactions: RefreshTransactionsUseCase,
    private val appEventBus: AppEventBus
) : ViewModel() {

    private val _uiState = MutableStateFlow(IncomesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Объединяем потоки данных от транзакций и счета,
        // чтобы иметь полную информацию для построения состояния экрана.
        viewModelScope.launch {
            combine(
                getIncomeTransactions(),
                getMainAccount()
            ) { transactionsResult, accountResult ->
                processResults(transactionsResult, accountResult)
            }.collect()
        }

        loadIncomes(isInitialLoad = true)
        observeAccountUpdates()
    }

    private fun observeAccountUpdates() {
        viewModelScope.launch {
            appEventBus.events.collect { event ->
                if (event is AppEvent.AccountUpdated) {
                    loadIncomes(forceRefresh = true)
                }
            }
        }
    }

    fun loadIncomes(isInitialLoad: Boolean = false, forceRefresh: Boolean = false) {
        val state = _uiState.value
        if ((state.isLoading || state.isRefreshing) && !forceRefresh) return

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
            val result = refreshTransactions(today, today)

            if (result is Result.Error) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = result.error
                    )
                }
            }
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

        // Обрабатываем ошибку, только если нечего показывать
        if (error != null && _uiState.value.transactions.isEmpty()) {
            _uiState.update {
                it.copy(isLoading = false, isRefreshing = false, error = error)
            }
            return
        }

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
        }
    }
}