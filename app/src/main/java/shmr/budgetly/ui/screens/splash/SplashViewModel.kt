package shmr.budgetly.ui.screens.splash

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel для сплэш-экрана.
 * Отвечает за управление состоянием готовности, которое сигнализирует
 * о завершении загрузки ресурсов (например, Lottie-анимации) и позволяет
 * продолжить навигацию.
 */
class SplashViewModel : ViewModel() {
    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    /**
     * Устанавливает состояние готовности в `true`.
     * Вызывается из UI, когда необходимые для отображения ресурсы загружены.
     */
    fun setReady() {
        _isReady.value = true
    }
}