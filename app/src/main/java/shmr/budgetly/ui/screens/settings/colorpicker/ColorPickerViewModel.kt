package shmr.budgetly.ui.screens.settings.colorpicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shmr.budgetly.domain.model.ThemeColor
import shmr.budgetly.domain.usecase.GetThemeColorUseCase
import shmr.budgetly.domain.usecase.SetThemeColorUseCase
import javax.inject.Inject


class ColorPickerViewModel @Inject constructor(
    private val getThemeColor: GetThemeColorUseCase,
    private val setThemeColor: SetThemeColorUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ColorPickerUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Подписываемся на изменения цвета темы из репозитория
        viewModelScope.launch {
            getThemeColor().collect { color ->
                _uiState.update { it.copy(selectedColor = color) }
            }
        }
    }

    /**
     * Обрабатывает выбор нового цвета пользователем.
     * Сохраняет выбор с помощью use case.
     */
    fun onColorSelected(color: ThemeColor) {
        viewModelScope.launch {
            setThemeColor(color)
        }
    }
}