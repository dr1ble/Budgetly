package shmr.budgetly.ui.screens.settings

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import shmr.budgetly.R
import javax.inject.Inject

/**
 * Уникальный идентификатор для элемента настроек.
 */
typealias SettingId = Int

/**
 * Тип элемента настроек, определяющий его поведение в UI.
 */
enum class SettingType {
    THEME_SWITCH, NAVIGATION
}

/**
 * Модель данных для одного элемента на экране настроек.
 * @param id Уникальный идентификатор.
 * @param titleRes Ресурс строки для заголовка.
 * @param type Тип элемента.
 */
data class SettingItem(
    val id: SettingId,
    @StringRes val titleRes: Int,
    val type: SettingType
)

/**
 * Состояние UI для экрана настроек.
 */
data class SettingsUiState(
    val settingsItems: List<SettingItem> = emptyList(),
    val isDarkThemeEnabled: Boolean = false // В будущем будет браться из SharedPreferences
)

/**
 * ViewModel для экрана "Настройки".
 * Отвечает за:
 * 1. Формирование списка настроек для отображения.
 * 2. Обработку действий пользователя, таких как переключение темы.
 * 3. Управление состоянием UI ([SettingsUiState]).
 */
@HiltViewModel
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
        _uiState.value = SettingsUiState(
            settingsItems = items,
            isDarkThemeEnabled = false // Начальное значение
        )
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