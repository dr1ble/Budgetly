package shmr.budgetly.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shmr.budgetly.R
import shmr.budgetly.domain.repository.UserPreferencesRepository
import shmr.budgetly.domain.usecase.IsPinSetUseCase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val isPinSetUseCase: IsPinSetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    val isPinSet: StateFlow<Boolean> = flow { emit(isPinSetUseCase()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val dateFormatter = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("ru"))

    init {
        viewModelScope.launch {
            isPinSet.collect { pinIsSet ->
                _uiState.update { it.copy(settingsItems = buildSettingsList(pinIsSet)) }
            }
        }
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

    private fun buildSettingsList(isPinSet: Boolean): List<SettingItem> {
        val list = mutableListOf(
            SettingItem(1, R.string.setting_dark_theme, SettingType.THEME_SWITCH),
            SettingItem(2, R.string.setting_primary_color, SettingType.PRIMARY_COLOR),
            SettingItem(4, R.string.setting_haptics, SettingType.NAVIGATION_HAPTICS),
        )
        list.add(SettingItem(5, R.string.setting_passcode, SettingType.NAVIGATION_PINCODE))

        list.addAll(
            listOf(
            SettingItem(6, R.string.setting_sync, SettingType.SYNC_INFO),
            SettingItem(7, R.string.setting_language, SettingType.NAVIGATION),
                SettingItem(8, R.string.setting_about, SettingType.NAVIGATION)
            )
        )

        return list
    }

    fun onThemeChanged(isDark: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setDarkTheme(isDark)
        }
    }
}