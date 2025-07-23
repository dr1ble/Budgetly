package shmr.budgetly.ui.screens.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import shmr.budgetly.domain.model.HapticEffect
import shmr.budgetly.domain.model.ThemeColor
import shmr.budgetly.domain.monitor.HapticFeedbackManager
import shmr.budgetly.domain.repository.UserPreferencesRepository
import shmr.budgetly.domain.usecase.GetHapticSettingsUseCase
import shmr.budgetly.domain.usecase.GetThemeColorUseCase
import shmr.budgetly.domain.usecase.HapticSettings
import javax.inject.Inject

/**
 * Глобальная ViewModel для MainActivity, предоставляющая общие состояния UI,
 * такие как настройка темы.
 */
class MainViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    getThemeColorUseCase: GetThemeColorUseCase,
    getHapticSettingsUseCase: GetHapticSettingsUseCase,
    private val hapticFeedbackManager: HapticFeedbackManager
) : ViewModel() {

    /**
     * Поток, который сообщает, включена ли темная тема.
     */
    val isDarkTheme: StateFlow<Boolean> = userPreferencesRepository.isDarkThemeEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    /**
     * Поток с выбранным основным цветом темы.
     */
    val themeColor: StateFlow<ThemeColor> = getThemeColorUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ThemeColor.GREEN
        )

    private val _hapticSettings =
        MutableStateFlow(HapticSettings(isEnabled = true, effect = HapticEffect.CLICK))

    /**
     * Поток с текущими настройками вибрации. Всегда содержит актуальные данные.
     */
    val hapticSettings: StateFlow<HapticSettings> = _hapticSettings.asStateFlow()

    init {
        viewModelScope.launch {
            getHapticSettingsUseCase().collect { settings ->
                _hapticSettings.value = settings
            }
        }
    }

    /**
     * Воспроизводит тактильный отклик в соответствии с настройками пользователя.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun performHapticFeedback() {
        if (hapticSettings.value.isEnabled) {
            hapticFeedbackManager.performHapticEffect(hapticSettings.value.effect)
        }
    }
}