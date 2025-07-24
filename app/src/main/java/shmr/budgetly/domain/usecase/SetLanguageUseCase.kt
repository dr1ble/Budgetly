package shmr.budgetly.domain.usecase

import shmr.budgetly.domain.model.Language
import shmr.budgetly.domain.repository.UserPreferencesRepository
import javax.inject.Inject

/**
 * UseCase для сохранения выбранного языка приложения.
 */
class SetLanguageUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(language: Language) = repository.setLanguage(language)
}