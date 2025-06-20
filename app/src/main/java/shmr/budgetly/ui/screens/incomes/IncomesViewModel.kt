package shmr.budgetly.ui.screens.incomes

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.repository.BudgetlyRepository
import javax.inject.Inject

data class IncomesUiState(
    val transactions: List<Transaction> = emptyList(),
    val totalAmount: String = "0 ₽",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class IncomesViewModel @Inject constructor(
    private val repository: BudgetlyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IncomesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadIncomes()
    }

    private fun loadIncomes() {
        _uiState.value = IncomesUiState(isLoading = true)

        val transactions = repository.getIncomeTransactions()
        val total = "600 000 ₽" // TODO: Implement real calculation

        _uiState.value = IncomesUiState(
            transactions = transactions,
            totalAmount = total,
            isLoading = false
        )
    }
}