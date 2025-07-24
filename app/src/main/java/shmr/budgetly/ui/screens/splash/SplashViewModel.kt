package shmr.budgetly.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shmr.budgetly.domain.usecase.IsPinSetUseCase
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
            _uiState.update { it.copy(isPinSet = isPinSet) }
        }
    }

    /**
     * Устанавливает состояние готовности в `true`.
     * Вызывается из UI, когда необходимые для отображения ресурсы (анимация) загружены.
     */
    fun setReady() {
        _uiState.update { it.copy(isAnimationReady = true) }
    }
}

data class SplashUiState(
    val isAnimationReady: Boolean = false,
    val isPinSet: Boolean? = null // null пока идет проверка
) {
    /**
     * Готовность к переходу на следующий экран.
     * Наступает, когда и анимация готова, и проверка пин-кода завершена.
     */
    val isReadyToNavigate: Boolean
        get() = isAnimationReady && isPinSet != null
}