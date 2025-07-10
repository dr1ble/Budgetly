package shmr.budgetly.ui.screens.history

import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.util.DomainError
import shmr.budgetly.ui.util.DatePickerDialogType
import java.time.LocalDate

data class HistoryUiState(
    val transactionsByDate: Map<LocalDate, List<Transaction>> = emptyMap(),
    val startDate: LocalDate = LocalDate.now().withDayOfMonth(1),
    val endDate: LocalDate = LocalDate.now(),
    val currencySymbol: String = "",
    val isLoading: Boolean = false,
    val datePickerType: DatePickerDialogType? = null,
    val error: DomainError? = null
) {
    /**
     * Флаг, показывающий, должен ли быть виден DatePicker.
     */
    val isDatePickerVisible: Boolean get() = datePickerType != null

    /**
     * Вычисляемое свойство для общей суммы.
     * Форматирует сумму всех транзакций из текущего состояния [transactionsByDate].
     * Возвращает "0" с символом валюты, если транзакций нет.
     */
    val totalSum: String get() {
        if (transactionsByDate.isEmpty()) return "0 $currencySymbol"
        val total = transactionsByDate.values.flatten().sumOf {
            it.amount.replace(Regex("[^0-9.-]"), "").toDoubleOrNull() ?: 0.0
        }
        // Форматируем с пробелами как разделителями тысяч
        return "%,.0f %s".format(total, currencySymbol).replace(",", " ")
    }
}