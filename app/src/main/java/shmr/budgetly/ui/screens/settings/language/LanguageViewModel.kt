package shmr.budgetly.ui.screens.settings.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shmr.budgetly.domain.model.Language
import shmr.budgetly.domain.usecase.GetLanguageUseCase
import shmr.budgetly.domain.usecase.SetLanguageUseCase
import javax.inject.Inject

class LanguageViewModel @Inject constructor(
    private val getLanguageUseCase: GetLanguageUseCase,
    private val setLanguageUseCase: SetLanguageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LanguageUiState())
    val uiState = _uiState.asStateFlow()

    private val _recreateActivityEvent = MutableSharedFlow<Unit>()
    val recreateActivityEvent = _recreateActivityEvent.asSharedFlow()

    init {
        observeCurrentLanguage()
    }

    private fun observeCurrentLanguage() {
        getLanguageUseCase()
            .onEach { language ->
                _uiState.update { it.copy(selectedLanguage = language) }
            }
            .launchIn(viewModelScope)
    }

    fun onLanguageSelected(language: Language) {
        val currentLanguage = _uiState.value.selectedLanguage
        if (language == currentLanguage) return

        viewModelScope.launch {
            // Сначала дожидаемся завершения асинхронной операции сохранения
            setLanguageUseCase(language)
            // И только потом отправляем событие для перезапуска
            _recreateActivityEvent.emit(Unit)
        }
    }
}