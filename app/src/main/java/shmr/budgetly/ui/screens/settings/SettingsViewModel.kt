package shmr.budgetly.ui.screens.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import shmr.budgetly.R
import javax.inject.Inject

/**
 * ViewModel для экрана "Настройки".
 * Отвечает за:
 * 1. Формирование списка настроек для отображения.
 * 2. Обработку действий пользователя, таких как переключение темы.
 * 3. Управление состоянием UI ([SettingsUiState]).
 */

class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    /**
     * Загружает и формирует список элементов настроек.
     */
    private fun loadSettings() {
        val items = listOf(
            SettingItem(1, R.string.setting_dark_theme, SettingType.THEME_SWITCH),
            SettingItem(2, R.string.setting_primary_color, SettingType.NAVIGATION),
            SettingItem(3, R.string.setting_sounds, SettingType.NAVIGATION),
            SettingItem(4, R.string.setting_haptics, SettingType.NAVIGATION),
            SettingItem(5, R.string.setting_passcode, SettingType.NAVIGATION),
            SettingItem(6, R.string.setting_sync, SettingType.NAVIGATION),
            SettingItem(7, R.string.setting_language, SettingType.NAVIGATION),
            SettingItem(8, R.string.setting_about, SettingType.NAVIGATION)
        )
        _uiState.update {
            it.copy(
                settingsItems = items,
                isDarkThemeEnabled = false // Начальное значение
            )
        }
    }

    /**
     * Обрабатывает изменение состояния переключателя темы.
     */
    fun onThemeChanged(isDark: Boolean) {
        // TODO: Сохранять выбор темы в постоянное хранилище (SharedPreferences/DataStore)
        _uiState.update { currentState ->
            currentState.copy(isDarkThemeEnabled = isDark)
        }
    }
}