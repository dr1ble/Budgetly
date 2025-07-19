package shmr.budgetly.ui.screens.account

import shmr.budgetly.domain.entity.Account
import shmr.budgetly.domain.util.DomainError

data class AccountUiState(
    val account: Account? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: DomainError? = null
)