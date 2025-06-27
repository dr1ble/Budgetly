package shmr.budgetly.ui.screens.incomes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.usecase.GetIncomeTransactionsUseCase
import shmr.budgetly.domain.util.DomainError
import shmr.budgetly.domain.util.Result
import javax.inject.Inject

data class IncomesUiState(
    val transactions: List<Transaction> = emptyList(),
    val totalAmount: String = "0 ₽",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: DomainError? = null
)

/**
 * ViewModel для экрана "Доходы".
 * Отвечает за загрузку списка транзакций-доходов через GetIncomeTransactionsUseCase,
 * управление состоянием UI и обработку pull-to-refresh.
 */
@HiltViewModel
class IncomesViewModel @Inject constructor(
    private val getIncomeTransactions: GetIncomeTransactionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(IncomesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadIncomes(isInitialLoad = true)
    }

    fun loadIncomes(isInitialLoad: Boolean = false) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = isInitialLoad,
                    isRefreshing = !isInitialLoad,
                    error = null
                )
            }
            processResult(getIncomeTransactions())
        }
    }

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

            is Result.Error -> {
                _uiState.update {
                    it.copy(isLoading = false, isRefreshing = false, error = result.error)
                }
            }
        }
    }
}