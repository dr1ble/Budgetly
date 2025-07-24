package shmr.budgetly.ui.screens.settings.language

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import shmr.budgetly.domain.model.Language
import shmr.budgetly.domain.usecase.GetLanguageUseCase
import javax.inject.Inject

class LanguageViewModel @Inject constructor(
    private val getLanguageUseCase: GetLanguageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LanguageUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val currentLanguage = getLanguageUseCase()
        _uiState.update { it.copy(selectedLanguage = currentLanguage) }
    }

    fun onLanguageSelected(language: Language) {
        if (language == _uiState.value.selectedLanguage) return

        _uiState.update { it.copy(selectedLanguage = language) }

        val appLocale = LocaleListCompat.forLanguageTags(language.code)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }
}