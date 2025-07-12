package shmr.budgetly.ui.screens.expenses

import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.util.DomainError

data class ExpensesUiState(
    val transactions: List<Transaction> = emptyList(),
    val totalAmount: String = "0",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: DomainError? = null
)