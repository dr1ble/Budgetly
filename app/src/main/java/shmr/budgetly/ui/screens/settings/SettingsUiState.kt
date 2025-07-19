package shmr.budgetly.ui.screens.settings

import androidx.annotation.StringRes

/**
 * Уникальный идентификатор для элемента настроек.
 */
typealias SettingId = Int

/**
 * Тип элемента настроек, определяющий его поведение в UI.
 */
enum class SettingType {
    THEME_SWITCH, NAVIGATION, SYNC_INFO
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
    val isDarkThemeEnabled: Boolean = false,
    val lastSyncTime: String = "Загрузка..."
)