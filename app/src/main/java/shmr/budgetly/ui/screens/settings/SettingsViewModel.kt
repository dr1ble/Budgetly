package shmr.budgetly.ui.screens.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import shmr.budgetly.data.MockData
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
        _uiState.value = SettingsUiState(
            settingsItems = MockData.settingsItems,
            isDarkThemeEnabled = false
        )
    }

    fun onThemeChanged(isDark: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(isDarkThemeEnabled = isDark)
        }
    }
}