package shmr.budgetly.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
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

    private val _uiState = MutableStateFlow(SettingsUiState(settingsItems = buildSettingsList()))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val dateFormatter = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("ru"))

    init {
        viewModelScope.launch {
            combine(
                userPreferencesRepository.isDarkThemeEnabled,
                userPreferencesRepository.lastSyncTimestamp
            ) { isDarkTheme, lastSync ->
                _uiState.update {
                    it.copy(
                        isDarkThemeEnabled = isDarkTheme,
                        lastSyncTime = formatSyncTime(lastSync)
                    )
                }
            }.collect()
        }
    }

    private fun formatSyncTime(timestamp: Long): String {
        return if (timestamp == 0L) "Никогда" else dateFormatter.format(Date(timestamp))
    }

    private fun buildSettingsList(): List<SettingItem> {
        return listOf(
            SettingItem(1, R.string.setting_dark_theme, SettingType.THEME_SWITCH),
            SettingItem(2, R.string.setting_primary_color, SettingType.PRIMARY_COLOR),
            SettingItem(4, R.string.setting_haptics, SettingType.NAVIGATION_HAPTICS),
            SettingItem(5, R.string.setting_passcode, SettingType.NAVIGATION_PINCODE),
            SettingItem(6, R.string.setting_sync, SettingType.SYNC_INFO),
            SettingItem(7, R.string.setting_language, SettingType.NAVIGATION),
            SettingItem(8, R.string.setting_about, SettingType.NAVIGATION)
        )
    }

    fun onThemeChanged(isDark: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setDarkTheme(isDark)
        }
    }
}