package shmr.budgetly.domain.usecase

import shmr.budgetly.domain.model.ThemeColor
import shmr.budgetly.domain.repository.UserPreferencesRepository
import javax.inject.Inject

/**
 * UseCase для сохранения выбранного цвета темы.
 */
class SetThemeColorUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(color: ThemeColor) = repository.setThemeColor(color)
}