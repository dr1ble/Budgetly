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
    THEME_SWITCH, PRIMARY_COLOR, NAVIGATION, SYNC_INFO, NAVIGATION_HAPTICS, NAVIGATION_PINCODE,
    NAVIGATION_LANGUAGE, NAVIGATION_ABOUT
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