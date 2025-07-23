package shmr.budgetly.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import shmr.budgetly.domain.repository.UserPreferencesRepository
import javax.inject.Inject

/**
 * Глобальная ViewModel для MainActivity, предоставляющая общие состояния UI,
 * такие как настройка темы.
 */
class MainViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    /**
     * Поток, который сообщает, включена ли темная тема.
     * `stateIn` кэширует последнее значение и делает Flow "горячим",
     * пока есть подписчики.
     */
    val isDarkTheme: StateFlow<Boolean> = userPreferencesRepository.isDarkThemeEnabled
        .stateIn(
            scope = viewModelScope,
            // Начинаем наблюдение, когда UI виден, и останавливаемся через 5 секунд после того, как он скрыт.
            started = SharingStarted.WhileSubscribed(5_000),
            // По умолчанию используется светлая тема, пока не загрузится значение из DataStore.
            initialValue = false
        )
}