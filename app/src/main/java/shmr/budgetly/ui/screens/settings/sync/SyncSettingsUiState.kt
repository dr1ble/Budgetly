package shmr.budgetly.ui.screens.settings.sync

import shmr.budgetly.domain.model.SyncInterval

/**
 * Состояние UI для экрана настроек частоты синхронизации.
 *
 * @param selectedInterval Текущий выбранный интервал.
 * @param sliderPosition Позиция слайдера (от 0.0f до N-1, где N - кол-во интервалов).
 * @param availableIntervals Список всех доступных интервалов.
 */
data class SyncSettingsUiState(
    val selectedInterval: SyncInterval = SyncInterval.default,
    val sliderPosition: Float = 0f,
    val availableIntervals: List<SyncInterval> = SyncInterval.entries.toList()
)