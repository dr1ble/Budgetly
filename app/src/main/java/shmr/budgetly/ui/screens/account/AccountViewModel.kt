package shmr.budgetly.ui.screens.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shmr.budgetly.domain.entity.Account
import shmr.budgetly.domain.usecase.GetMainAccountUseCase
import shmr.budgetly.domain.usecase.RefreshMainAccountUseCase
import shmr.budgetly.domain.util.Result
import javax.inject.Inject

/**
 * ViewModel для экрана "Счет".
 * Отвечает за:
 * 1. Загрузку данных об основном счете пользователя через [GetMainAccountUseCase].
 * 2. Управление состоянием UI ([AccountUiState]), включая состояния загрузки и ошибок.
 * 3. Обработку действий пользователя, таких как повторная загрузка данных.
 */

class AccountViewModel @Inject constructor(
    private val getMainAccount: GetMainAccountUseCase,
    private val refreshMainAccount: RefreshMainAccountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getMainAccount().collect { result ->
                processResult(result)
            }
        }
        loadAccount(isInitialLoad = true)
    }


    /**
     * Инициирует загрузку данных о счете.
     * @param isInitialLoad true, если это первая загрузка (показывает полноэкранный индикатор),
     * false для фоновых обновлений.
     */
    fun loadAccount(isInitialLoad: Boolean = false) {
        val showLoading = isInitialLoad && _uiState.value.account == null
        val showRefreshing = !isInitialLoad || _uiState.value.account != null

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = showLoading,
                    isRefreshing = showRefreshing,
                    error = null
                )
            }
            refreshMainAccount()
            _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
        }
    }

    /**
     * Обрабатывает результат, полученный от UseCase, и обновляет UI state.
     */
    private fun processResult(result: Result<Account>) {
        when (result) {
            is Result.Success -> _uiState.update {
                it.copy(
                    account = result.data,
                    isLoading = false,
                    isRefreshing = false,
                    error = null
                )
            }

            is Result.Error -> _uiState.update {
                if (it.account == null) {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = result.error
                    )
                } else {
                    it.copy(isRefreshing = false) // Скрываем индикатор, но оставляем старые данные
                }
            }
        }
    }
}