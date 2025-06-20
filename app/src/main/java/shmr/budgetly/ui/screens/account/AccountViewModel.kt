package shmr.budgetly.ui.screens.account

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import shmr.budgetly.domain.entity.Account
import shmr.budgetly.domain.repository.BudgetlyRepository
import javax.inject.Inject

data class AccountUiState(
    val account: Account? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val repository: BudgetlyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAccount()
    }

    private fun loadAccount() {
        _uiState.value = AccountUiState(isLoading = true)
        val account = repository.getMainAccount()
        _uiState.value = AccountUiState(account = account, isLoading = false)
    }
}