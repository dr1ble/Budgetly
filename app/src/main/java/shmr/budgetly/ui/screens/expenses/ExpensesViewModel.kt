package shmr.budgetly.ui.screens.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shmr.budgetly.domain.usecase.GetExpenseTransactionsUseCase
import shmr.budgetly.domain.usecase.GetMainAccountUseCase
import shmr.budgetly.domain.util.Result
import shmr.budgetly.ui.util.formatCurrencySymbol
import javax.inject.Inject

/**
 * ViewModel для экрана "Расходы".
 * Отвечает за:
 * 1. Загрузку списка транзакций-расходов за текущий месяц через [GetExpenseTransactionsUseCase].
 * 2. Управление состоянием UI ([ExpensesUiState]), включая флаги для первоначальной загрузки и pull-to-refresh.
 * 3. Расчет и форматирование общей суммы расходов.
 */

class ExpensesViewModel @Inject constructor(
    private val getExpenseTransactions: GetExpenseTransactionsUseCase,
    private val getMainAccount: GetMainAccountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpensesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Добавим начальную загрузку при инициализации ViewModel
        loadExpenses(isInitialLoad = true)
    }

    /**
     * Инициирует загрузку расходов.
     * @param isInitialLoad true для первоначальной загрузки (показывает полноэкранный индикатор).
     * @param forceRefresh true, чтобы принудительно обновить данные, даже если они уже есть.
     */
    fun loadExpenses(isInitialLoad: Boolean = false, forceRefresh: Boolean = false) {
        val state = _uiState.value

        // Улучшенная защита от лишних вызовов
        if ((state.isLoading || state.isRefreshing) && !forceRefresh) {
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = isInitialLoad,
                    isRefreshing = !isInitialLoad,
                    error = null
                )
            }

            val accountResultDeferred = async { getMainAccount() }
            val transactionsResultDeferred = async { getExpenseTransactions() }

            val accountResult = accountResultDeferred.await()
            val transactionsResult = transactionsResultDeferred.await()

            val error = (accountResult as? Result.Error)?.error
                ?: (transactionsResult as? Result.Error)?.error

            if (error != null) {
                _uiState.update { it.copy(isLoading = false, isRefreshing = false, error = error) }
                return@launch
            }

            val account = (accountResult as Result.Success).data
            val transactions = (transactionsResult as Result.Success).data

            val currencySymbol = formatCurrencySymbol(account.currency)
            val total = transactions.sumOf {
                it.amount.replace(Regex("[^0-9.-]"), "").toDoubleOrNull() ?: 0.0
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isRefreshing = false,
                    transactions = transactions,
                    totalAmount = "%,.0f %s".format(total, currencySymbol).replace(",", " ")
                )
            }
        }
    }
}