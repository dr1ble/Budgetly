package shmr.budgetly.ui.screens.expenses

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.repository.BudgetlyRepository
import javax.inject.Inject

data class ExpensesUiState(
    val transactions: List<Transaction> = emptyList(),
    val totalAmount: String = "0 ₽",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val repository: BudgetlyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpensesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadExpenses()
    }

    private fun loadExpenses() {
        _uiState.value = ExpensesUiState(isLoading = true)
        val transactions = repository.getExpenseTransactions()
        val total = "436 558 ₽" // TODO: Implement real calculation
        _uiState.value = ExpensesUiState(
            transactions = transactions,
            totalAmount = total,
            isLoading = false
        )
    }
}