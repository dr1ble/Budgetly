package shmr.budgetly.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import shmr.budgetly.domain.model.ThemeColor
import shmr.budgetly.domain.repository.UserPreferencesRepository
import shmr.budgetly.domain.usecase.GetThemeColorUseCase
import javax.inject.Inject

/**
 * Глобальная ViewModel для MainActivity, предоставляющая общие состояния UI,
 * такие как настройка темы.
 */
class MainViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    getThemeColorUseCase: GetThemeColorUseCase
) : ViewModel() {

    /**
     * Поток, который сообщает, включена ли темная тема.
     */
    val isDarkTheme: StateFlow<Boolean> = userPreferencesRepository.isDarkThemeEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    /**
     * Поток с выбранным основным цветом темы.
     */
    val themeColor: StateFlow<ThemeColor> = getThemeColorUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ThemeColor.GREEN
        )
}