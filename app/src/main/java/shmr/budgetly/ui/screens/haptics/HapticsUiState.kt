package shmr.budgetly.ui.screens.haptics

import shmr.budgetly.domain.model.HapticEffect

/**
 * Состояние UI для экрана настроек тактильной обратной связи.
 */
data class HapticsUiState(
    val isEnabled: Boolean = true,
    val selectedEffect: HapticEffect = HapticEffect.CLICK,
    val availableEffects: List<HapticEffect> = HapticEffect.entries
)