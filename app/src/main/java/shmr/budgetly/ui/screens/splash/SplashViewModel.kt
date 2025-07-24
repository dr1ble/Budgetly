package shmr.budgetly.ui.screens.splash

import SplashUiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shmr.budgetly.domain.usecase.IsPinSetUseCase
import shmr.budgetly.ui.navigation.Main
import shmr.budgetly.ui.navigation.Pin
import shmr.budgetly.ui.navigation.PinScreenPurpose
import javax.inject.Inject

/**
 * ViewModel для сплэш-экрана.
 * Отвечает за управление состоянием готовности и проверку,
 * установлен ли пин-код для определения следующего экрана.
 */
class SplashViewModel @Inject constructor(
    private val isPinSetUseCase: IsPinSetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState = _uiState.asStateFlow()

    init {
        checkPinStatus()
    }

    private fun checkPinStatus() {
        viewModelScope.launch {
            val isPinSet = isPinSetUseCase()
            val destination = if (isPinSet) Pin(PinScreenPurpose.UNLOCK) else Main
            _uiState.update { it.copy(isDataLoaded = true, navigationDestination = destination) }
        }
    }

    /**
     * Вызывается из UI по завершении Lottie-анимации.
     */
    fun onAnimationFinished() {
        _uiState.update { it.copy(isNavigationAllowed = true) }
    }
}