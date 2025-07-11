package shmr.budgetly.ui.screens.transactiondetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shmr.budgetly.domain.entity.Category
import shmr.budgetly.domain.usecase.CreateTransactionUseCase
import shmr.budgetly.domain.usecase.DeleteTransactionUseCase
import shmr.budgetly.domain.usecase.GetAllCategoriesUseCase
import shmr.budgetly.domain.usecase.GetMainAccountUseCase
import shmr.budgetly.domain.usecase.GetTransactionUseCase
import shmr.budgetly.domain.usecase.UpdateTransactionUseCase
import shmr.budgetly.domain.util.Result
import shmr.budgetly.ui.navigation.TransactionDetails
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Locale
import javax.inject.Inject

class TransactionDetailsViewModel @Inject constructor(
    private val getTransactionUseCase: GetTransactionUseCase,
    private val createTransactionUseCase: CreateTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val getMainAccountUseCase: GetMainAccountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionDetailsUiState())
    val uiState = _uiState.asStateFlow()

    var transactionId: Int? = null

    /**
     * Инициализация ViewModel. Определяет режим (создание/редактирование) и загружает данные.
     */
    fun init(navArgs: TransactionDetails) {
        if (transactionId != null && transactionId == navArgs.transactionId) return

        transactionId = navArgs.transactionId
        val isEditMode = navArgs.transactionId != null
        _uiState.update {
            it.copy(
                isEditMode = isEditMode,
                isIncome = navArgs.isIncome,
                parentRoute = navArgs.parentRoute
            )
        }

        if (isEditMode) {
            loadTransactionDetails(navArgs.transactionId)
        } else {
            loadDataForCreation()
        }
    }

    private fun loadDataForCreation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val accountResultDeferred = async { getMainAccountUseCase() }
            val categoriesResultDeferred = async { getAllCategoriesUseCase() }

            val accountResult = accountResultDeferred.await()
            val categoriesResult = categoriesResultDeferred.await()


            val error = (accountResult as? Result.Error)?.error
                ?: (categoriesResult as? Result.Error)?.error

            if (error != null) {
                _uiState.update { it.copy(isLoading = false, error = error) }
                return@launch
            }

            val account = (accountResult as Result.Success).data
            val allCategories = (categoriesResult as Result.Success).data
            val filteredCategories = allCategories.filter { it.isIncome == _uiState.value.isIncome }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    selectedAccount = account,
                    availableAccounts = listOf(account),
                    availableCategories = filteredCategories
                )
            }
        }
    }

    private fun loadTransactionDetails(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val transactionResultDeferred = async { getTransactionUseCase(id) }
            val accountResultDeferred = async { getMainAccountUseCase() }
            val categoriesResultDeferred = async { getAllCategoriesUseCase() }

            val transactionResult = transactionResultDeferred.await()
            val accountResult = accountResultDeferred.await()
            val categoriesResult = categoriesResultDeferred.await()

            val error = (transactionResult as? Result.Error)?.error
                ?: (accountResult as? Result.Error)?.error
                ?: (categoriesResult as? Result.Error)?.error

            if (error != null) {
                _uiState.update { it.copy(isLoading = false, error = error) }
                return@launch
            }

            val transaction = (transactionResult as Result.Success).data
            val account = (accountResult as Result.Success).data
            val allCategories = (categoriesResult as Result.Success).data
            val filteredCategories =
                allCategories.filter { it.isIncome == transaction.category.isIncome }

            val amountForEditing = transaction.amount.replace(',', '.')


            _uiState.update {
                it.copy(
                    isLoading = false,
                    amount = amountForEditing,
                    selectedAccount = account,
                    selectedCategory = transaction.category,
                    date = transaction.transactionDate.toLocalDate(),
                    time = transaction.transactionDate.toLocalTime(),
                    comment = transaction.comment,
                    availableAccounts = listOf(account),
                    availableCategories = filteredCategories,
                )
            }
        }
    }

    fun onAmountChange(newAmount: String) {
        val sanitizedAmount =
            if (newAmount.startsWith("0") && newAmount.length > 1 && newAmount[1] != '.') {
                newAmount.substring(1)
            } else {
                newAmount
            }
        val regex = "^[0-9]*([.,][0-9]{0,2})?$".toRegex()
        if (sanitizedAmount.matches(regex)) {
            _uiState.update { it.copy(amount = sanitizedAmount.replace(',', '.')) }
        }
    }

//    fun onAccountSelected(account: Account) {
//        _uiState.update { it.copy(selectedAccount = account, isAccountPickerVisible = false) }
//    }

    fun onCategorySelected(category: Category) {
        _uiState.update { it.copy(selectedCategory = category, isCategoryPickerVisible = false) }
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(date = date, isDatePickerVisible = false) }
    }

    fun onTimeSelected(time: LocalTime) {
        _uiState.update { it.copy(time = time, isTimePickerVisible = false) }
    }

    fun onCommentChange(newComment: String) {
        _uiState.update { it.copy(comment = newComment) }
    }

    fun onSaveClick() {
        if (!_uiState.value.isSaveEnabled) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, saveError = null) }

            val state = _uiState.value

            val amountToSend = try {
                val symbols = DecimalFormatSymbols(Locale.US)
                val decimalFormat = DecimalFormat("0.00", symbols)
                decimalFormat.format(BigDecimal(state.amount.replace(',', '.')))
            } catch (_: Exception) {
                state.amount
            }

            val transactionDateTime = LocalDateTime.of(state.date, state.time)

            val commentToSend = state.comment

            val result = if (state.isEditMode) {
                updateTransactionUseCase(
                    id = transactionId!!,
                    accountId = state.selectedAccount!!.id,
                    categoryId = state.selectedCategory!!.id,
                    amount = amountToSend,
                    transactionDate = transactionDateTime,
                    comment = commentToSend
                )
            } else {
                createTransactionUseCase(
                    accountId = state.selectedAccount!!.id,
                    categoryId = state.selectedCategory!!.id,
                    amount = amountToSend,
                    transactionDate = transactionDateTime,
                    comment = commentToSend
                )
            }

            when (result) {
                is Result.Success -> _uiState.update {
                    it.copy(
                        isSaving = false,
                        isSaveSuccess = true
                    )
                }

                is Result.Error -> _uiState.update {
                    it.copy(
                        isSaving = false,
                        saveError = result.error
                    )
                }
            }
        }
    }

    fun showDeleteConfirmDialog() = _uiState.update { it.copy(showDeleteConfirmDialog = true) }
    fun dismissDeleteConfirmDialog() = _uiState.update { it.copy(showDeleteConfirmDialog = false) }

    fun deleteTransaction() {
        if (!_uiState.value.isEditMode) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true,
                    saveError = null,
                    showDeleteConfirmDialog = false
                )
            }
            val result = deleteTransactionUseCase(transactionId!!)
            when (result) {
                is Result.Success -> _uiState.update {
                    it.copy(
                        isSaving = false,
                        isSaveSuccess = true
                    )
                }

                is Result.Error -> _uiState.update {
                    it.copy(
                        isSaving = false,
                        saveError = result.error
                    )
                }
            }
        }
    }

    fun showDatePicker() = _uiState.update { it.copy(isDatePickerVisible = true) }
    fun dismissDatePicker() = _uiState.update { it.copy(isDatePickerVisible = false) }

    fun showTimePicker() = _uiState.update { it.copy(isTimePickerVisible = true) }
    fun dismissTimePicker() = _uiState.update { it.copy(isTimePickerVisible = false) }

//    fun showAccountPicker() = _uiState.update { it.copy(isAccountPickerVisible = true) }
//    fun dismissAccountPicker() = _uiState.update { it.copy(isAccountPickerVisible = false) }

    fun showCategoryPicker() = _uiState.update { it.copy(isCategoryPickerVisible = true) }
    fun dismissCategoryPicker() = _uiState.update { it.copy(isCategoryPickerVisible = false) }

    fun dismissSaveErrorDialog() = _uiState.update { it.copy(saveError = null) }
}