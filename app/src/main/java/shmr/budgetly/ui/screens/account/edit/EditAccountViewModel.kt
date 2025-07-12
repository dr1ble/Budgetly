package shmr.budgetly.ui.screens.account.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shmr.budgetly.domain.usecase.GetMainAccountUseCase
import shmr.budgetly.domain.usecase.UpdateAccountUseCase
import shmr.budgetly.domain.util.Result
import javax.inject.Inject

@HiltViewModel
class EditAccountViewModel @Inject constructor(
    private val getMainAccount: GetMainAccountUseCase,
    private val updateAccount: UpdateAccountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditAccountUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isInitialLoading = true, error = null) }
            when (val result = getMainAccount()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isInitialLoading = false,
                            initialAccount = result.data,
                            name = result.data.name,
                            balance = result.data.balance,
                            currency = result.data.currency
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isInitialLoading = false, error = result.error) }
                }
            }
        }
    }

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    fun onBalanceChange(newBalance: String) {
        _uiState.update { it.copy(balance = newBalance) }
    }

    fun onCurrencyChange(newCurrency: String) {
        _uiState.update { it.copy(currency = newCurrency) }
    }

    fun onCurrencyPickerShow() {
        _uiState.update { it.copy(isBottomSheetVisible = true) }
    }

    fun onCurrencyPickerDismiss() {
        _uiState.update { it.copy(isBottomSheetVisible = false) }
    }

    fun saveAccount() {
        val currentState = _uiState.value
        if (!currentState.isSaveEnabled) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = updateAccount(
                name = currentState.name!!,
                balance = currentState.balance!!,
                currency = currentState.currency!!
            )
            when (result) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, isSaveSuccess = true) }
                }

                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.error) }
                }
            }
        }
    }
}