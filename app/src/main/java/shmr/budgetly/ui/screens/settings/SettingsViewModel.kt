package shmr.budgetly.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shmr.budgetly.R
import shmr.budgetly.domain.repository.UserPreferencesRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    private val dateFormatter = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("ru"))

    init {
        loadSettings()
        observeLastSyncTime()
    }

    private fun observeLastSyncTime() {
        viewModelScope.launch {
            userPreferencesRepository.lastSyncTimestamp.collect { timestamp ->
                val formattedTime = if (timestamp == 0L) {
                    "Никогда"
                } else {
                    dateFormatter.format(Date(timestamp))
                }
                _uiState.update { it.copy(lastSyncTime = formattedTime) }
            }
        }
    }

    private fun loadSettings() {
        val items = listOf(
            SettingItem(1, R.string.setting_dark_theme, SettingType.THEME_SWITCH),
            SettingItem(2, R.string.setting_primary_color, SettingType.NAVIGATION),
            SettingItem(3, R.string.setting_sounds, SettingType.NAVIGATION),
            SettingItem(4, R.string.setting_haptics, SettingType.NAVIGATION),
            SettingItem(5, R.string.setting_passcode, SettingType.NAVIGATION),
            SettingItem(6, R.string.setting_sync, SettingType.SYNC_INFO),
            SettingItem(7, R.string.setting_language, SettingType.NAVIGATION),
            SettingItem(8, R.string.setting_about, SettingType.NAVIGATION),
        )
        _uiState.update {
            it.copy(
                settingsItems = items,
                isDarkThemeEnabled = false
            )
        }
    }

    fun onThemeChanged(isDark: Boolean) {
        // TODO: Сохранять выбор темы в постоянное хранилище (SharedPreferences/DataStore)
        _uiState.update { currentState ->
            currentState.copy(isDarkThemeEnabled = isDark)
        }
    }
}