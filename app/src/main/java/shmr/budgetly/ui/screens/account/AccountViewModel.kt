package shmr.budgetly.ui.screens.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shmr.budgetly.domain.entity.Account
import shmr.budgetly.domain.usecase.GetMainAccountUseCase
import shmr.budgetly.domain.util.DomainError
import shmr.budgetly.domain.util.Result
import javax.inject.Inject

data class AccountUiState(
    val account: Account? = null,
    val isLoading: Boolean = false,
    val error: DomainError? = null
)

/**
 * ViewModel для экрана "Счет".
 * Отвечает за загрузку данных об основном счете пользователя через GetMainAccountUseCase,
 * управление состоянием UI и обработку действий пользователя.
 */
@HiltViewModel
class AccountViewModel @Inject constructor(
    private val getMainAccount: GetMainAccountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAccount(isInitialLoad = true)
    }

    fun loadAccount(isInitialLoad: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = isInitialLoad, error = null) }
            processResult(getMainAccount())
        }
    }

    private fun processResult(result: Result<Account>) {
        when (result) {
            is Result.Success -> {
                _uiState.update {
                    it.copy(account = result.data, isLoading = false)
                }
            }

            is Result.Error -> {
                _uiState.update {
                    it.copy(isLoading = false, error = result.error)
                }
            }
        }
    }
}