package shmr.budgetly.ui.screens.pincode

/**
 * Состояние UI для экрана настроек пин-кода.
 * @param isPinSet Установлен ли пин-код в данный момент. null означает, что состояние еще загружается.
 */
data class PinSettingsUiState(
    val isPinSet: Boolean? = null
)