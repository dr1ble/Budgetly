package shmr.budgetly.ui.screens.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.usecase.GetExpenseTransactionsUseCase
import shmr.budgetly.domain.util.DomainError
import shmr.budgetly.domain.util.Result
import javax.inject.Inject

data class ExpensesUiState(
    val transactions: List<Transaction> = emptyList(),
    val totalAmount: String = "0 ₽",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: DomainError? = null
)

/**
 * ViewModel для экрана "Расходы".
 * Отвечает за:
 * 1. Загрузку списка транзакций-расходов за текущий месяц через [GetExpenseTransactionsUseCase].
 * 2. Управление состоянием UI ([ExpensesUiState]), включая флаги для первоначальной загрузки и pull-to-refresh.
 * 3. Расчет и форматирование общей суммы расходов.
 */
@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val getExpenseTransactions: GetExpenseTransactionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpensesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadExpenses(isInitialLoad = true)
    }

    /**
     * Инициирует загрузку расходов.
     * @param isInitialLoad true для первоначальной загрузки, false для pull-to-refresh.
     */
    fun loadExpenses(isInitialLoad: Boolean = false) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = isInitialLoad,
                    isRefreshing = !isInitialLoad,
                    error = null
                )
            }
            processResult(getExpenseTransactions())
        }
    }

    /**
     * Обрабатывает результат загрузки и обновляет UI state.
     */
    private fun processResult(result: Result<List<Transaction>>) {
        when (result) {
            is Result.Success -> {
                val total = result.data.sumOf {
                    it.amount.replace(Regex("[^0-9.-]"), "").toDoubleOrNull() ?: 0.0
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        transactions = result.data,
                        totalAmount = "%,.0f ₽".format(total).replace(",", " ")
                    )
                }
            }
            is Result.Error -> _uiState.update {
                it.copy(isLoading = false, isRefreshing = false, error = result.error)
            }
        }
    }
}