package shmr.budgetly.ui.screens.settings.colorpicker

import shmr.budgetly.domain.model.ThemeColor

data class ColorPickerUiState(
    val availableColors: List<ThemeColor> = ThemeColor.entries,
    val selectedColor: ThemeColor = ThemeColor.GREEN
)