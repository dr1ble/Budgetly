package shmr.budgetly.ui.screens.analyze

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shmr.budgetly.R
import shmr.budgetly.di.viewmodel.AssistedSavedStateViewModelFactory
import shmr.budgetly.domain.model.TransactionFilterType
import shmr.budgetly.domain.usecase.AnalyzeTransactionsUseCase
import shmr.budgetly.domain.usecase.GetHistoryUseCase
import shmr.budgetly.domain.usecase.RefreshTransactionsUseCase
import shmr.budgetly.domain.util.Result
import shmr.budgetly.ui.navigation.Analyze
import shmr.budgetly.ui.navigation.Expenses
import shmr.budgetly.ui.navigation.Incomes
import shmr.budgetly.ui.util.DatePickerDialogType
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class AnalyzeViewModel @AssistedInject constructor(
    private val getHistory: GetHistoryUseCase,
    private val refreshTransactions: RefreshTransactionsUseCase,
    private val analyzeTransactions: AnalyzeTransactionsUseCase,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyzeUiState())
    val uiState = _uiState.asStateFlow()

    private var startDate: LocalDate
    private var endDate: LocalDate
    private val filterType: TransactionFilterType

    init {
        val navArgs: Analyze = savedStateHandle.toRoute()
        startDate = LocalDate.ofEpochDay(navArgs.startDate)
        endDate = LocalDate.ofEpochDay(navArgs.endDate)

        filterType = when (navArgs.parentRoute) {
            Expenses::class.qualifiedName -> TransactionFilterType.EXPENSE
            Incomes::class.qualifiedName -> TransactionFilterType.INCOME
            else -> TransactionFilterType.ALL
        }

        val titleResource = if (filterType == TransactionFilterType.INCOME) {
            R.string.analysis_income_title
        } else {
            R.string.analysis_expenses_title
        }

        // Обновляем UiState, передавая ID ресурса и даты
        _uiState.update {
            it.copy(
                titleRes = titleResource,
                startDate = startDate,
                endDate = endDate
            )
        }
        loadAnalysis()
    }

    @AssistedFactory
    interface Factory : AssistedSavedStateViewModelFactory<AnalyzeViewModel> {
        override fun create(savedStateHandle: SavedStateHandle): AnalyzeViewModel
    }

    fun onRetry() {
        loadAnalysis()
    }

    fun onStartDatePickerOpen() =
        _uiState.update { it.copy(datePickerType = DatePickerDialogType.START_DATE) }

    fun onEndDatePickerOpen() =
        _uiState.update { it.copy(datePickerType = DatePickerDialogType.END_DATE) }

    fun onDatePickerDismiss() = _uiState.update { it.copy(datePickerType = null) }

    fun onDateSelected(dateInMillis: Long?) {
        val selectedDate = dateInMillis?.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate()
        } ?: return onDatePickerDismiss()

        val currentDialog = _uiState.value.datePickerType
        var dateChanged = false

        when (currentDialog) {
            DatePickerDialogType.START_DATE -> {
                if (!selectedDate.isAfter(endDate)) {
                    startDate = selectedDate
                    dateChanged = true
                }
            }

            DatePickerDialogType.END_DATE -> {
                if (!selectedDate.isBefore(startDate)) {
                    endDate = selectedDate
                    dateChanged = true
                }
            }

            null -> {}
        }

        _uiState.update { it.copy(startDate = startDate, endDate = endDate) }
        onDatePickerDismiss()

        if (dateChanged) {
            loadAnalysis()
        }
    }

    private fun loadAnalysis() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, analysisResult = null) }

            refreshTransactions(startDate, endDate)

            getHistory(startDate, endDate, filterType).first().let { transactionsResult ->
                when (transactionsResult) {
                    is Result.Success -> {
                        val analysis = analyzeTransactions(transactionsResult.data)
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                analysisResult = analysis
                            )
                        }
                    }

                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = transactionsResult.error,
                            )
                        }
                    }
                }
            }
        }
    }
}