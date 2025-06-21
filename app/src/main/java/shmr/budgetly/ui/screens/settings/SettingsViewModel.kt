package shmr.budgetly.ui.screens.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SettingsUiState(
    val settingsItems: List<String> = emptyList(),
    val isDarkThemeEnabled: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        // Данные теперь живут здесь, а не в MockData
        val items = listOf(
            "Тёмная тема",
            "Основной цвет",
            "Звуки",
            "Хаптики",
            "Код пароль",
            "Синхронизация",
            "Язык",
            "О программе"
        )
        _uiState.value = SettingsUiState(
            settingsItems = items,
            isDarkThemeEnabled = false
        )
    }

    fun onThemeChanged(isDark: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(isDarkThemeEnabled = isDark)
        }
    }
}