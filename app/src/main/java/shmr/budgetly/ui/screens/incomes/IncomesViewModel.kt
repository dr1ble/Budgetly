package shmr.budgetly.ui.screens.incomes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shmr.budgetly.domain.usecase.GetIncomeTransactionsUseCase
import shmr.budgetly.domain.usecase.GetMainAccountUseCase
import shmr.budgetly.domain.util.Result
import shmr.budgetly.ui.util.formatCurrencySymbol
import javax.inject.Inject

/**
 * ViewModel для экрана "Доходы".
 * Отвечает за:
 * 1. Загрузку списка транзакций-доходов за текущий месяц через [GetIncomeTransactionsUseCase].
 * 2. Управление состоянием UI ([IncomesUiState]), включая флаги для первоначальной загрузки и pull-to-refresh.
 * 3. Расчет и форматирование общей суммы доходов.
 */
@HiltViewModel
class IncomesViewModel @Inject constructor(
    private val getIncomeTransactions: GetIncomeTransactionsUseCase,
    private val getMainAccount: GetMainAccountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(IncomesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadIncomes(isInitialLoad = true)
    }

    /**
     * Инициирует загрузку доходов.
     * @param isInitialLoad true для первоначальной загрузки, false для pull-to-refresh.
     */
    fun loadIncomes(isInitialLoad: Boolean = false) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = isInitialLoad,
                    isRefreshing = !isInitialLoad,
                    error = null
                )
            }

            val accountResultDeferred = async { getMainAccount() }
            val transactionsResultDeferred = async { getIncomeTransactions() }

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