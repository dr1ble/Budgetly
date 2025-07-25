package shmr.budgetly.ui.screens.settings.language

import shmr.budgetly.domain.model.Language

/**
 * Состояние UI для экрана выбора языка.
 */
data class LanguageUiState(
    val availableLanguages: List<Language> = Language.entries,
    val selectedLanguage: Language = Language.RUSSIAN
)