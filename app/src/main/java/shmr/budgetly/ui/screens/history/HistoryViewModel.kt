package shmr.budgetly.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.repository.BudgetlyRepository
import shmr.budgetly.domain.util.Result
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class HistoryUiState(
    val transactionsByDate: Map<LocalDate, List<Transaction>> = emptyMap(),
    val startDate: LocalDate = LocalDate.now().withDayOfMonth(1),
    val endDate: LocalDate = LocalDate.now(),
    val totalSum: String = "0 ₽",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: BudgetlyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadHistoryForCurrentPeriod()
    }

    fun onDatePeriodChanged(newStartDate: LocalDate, newEndDate: LocalDate) {
        _uiState.update {
            it.copy(startDate = newStartDate, endDate = newEndDate)
        }
        loadHistoryForCurrentPeriod()
    }

    private fun loadHistoryForCurrentPeriod() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val currentState = _uiState.value
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE

            when (val result = repository.getHistory(
                accountId = 1,
                startDate = currentState.startDate.format(formatter),
                endDate = currentState.endDate.format(formatter)
            )) {
                is Result.Success -> {
                    val transactions = result.data
                    val total = transactions.sumOf {
                        val amount =
                            it.amount.replace(Regex("[^0-9.-]"), "").toDoubleOrNull() ?: 0.0
                        if (it.category.isIncome) amount else -amount
                    }
                    val grouped = transactions.groupBy { it.transactionDate.toLocalDate() }

                    _uiState.update {
                        it.copy(
                            transactionsByDate = grouped,
                            totalSum = "%,.0f ₽".format(total).replace(",", " "),
                            isLoading = false
                        )
                    }
                }

                is Result.Error -> {
                    _uiState.update { it.copy(error = "Failed to load history", isLoading = false) }
                }
            }
        }
    }
}