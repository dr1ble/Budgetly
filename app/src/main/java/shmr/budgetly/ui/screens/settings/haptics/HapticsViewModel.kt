package shmr.budgetly.ui.screens.settings.haptics

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shmr.budgetly.domain.model.HapticEffect
import shmr.budgetly.domain.monitor.HapticFeedbackManager
import shmr.budgetly.domain.usecase.GetHapticSettingsUseCase
import shmr.budgetly.domain.usecase.SetHapticEffectUseCase
import shmr.budgetly.domain.usecase.SetHapticsEnabledUseCase
import javax.inject.Inject

class HapticsViewModel @Inject constructor(
    private val getHapticSettings: GetHapticSettingsUseCase,
    private val setHapticsEnabled: SetHapticsEnabledUseCase,
    private val setHapticEffect: SetHapticEffectUseCase,
    private val hapticFeedbackManager: HapticFeedbackManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HapticsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeHapticSettings()
    }

    private fun observeHapticSettings() {
        viewModelScope.launch {
            getHapticSettings().collect { settings ->
                _uiState.update {
                    it.copy(
                        isEnabled = settings.isEnabled,
                        selectedEffect = settings.effect
                    )
                }
            }
        }
    }

    /**
     * Обрабатывает переключение свитча включения/выключения вибрации.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun onHapticsEnabledChanged(isEnabled: Boolean) {
        viewModelScope.launch {
            setHapticsEnabled(isEnabled)
            // Воспроизводим эффект, чтобы пользователь понял, что настройка применилась
            if (isEnabled) {
                hapticFeedbackManager.performHapticEffect(_uiState.value.selectedEffect)
            }
        }
    }

    /**
     * Обрабатывает выбор нового эффекта вибрации.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun onEffectSelected(effect: HapticEffect) {
        viewModelScope.launch {
            setHapticEffect(effect)
            hapticFeedbackManager.performHapticEffect(effect)
        }
    }
}