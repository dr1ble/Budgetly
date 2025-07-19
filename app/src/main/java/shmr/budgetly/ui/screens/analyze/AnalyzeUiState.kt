package shmr.budgetly.ui.screens.analyze

import shmr.budgetly.domain.model.AnalysisResult
import shmr.budgetly.domain.util.DomainError
import shmr.budgetly.ui.util.DatePickerDialogType
import java.time.LocalDate

data class AnalyzeUiState(
    val isLoading: Boolean = false,
    val error: DomainError? = null,
    val analysisResult: AnalysisResult? = null,
    val title: String = "",
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = LocalDate.now(),
    val datePickerType: DatePickerDialogType? = null
) {
    val isDatePickerVisible: Boolean get() = datePickerType != null
}