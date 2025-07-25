package shmr.budgetly.domain.usecase

import shmr.budgetly.domain.repository.UserPreferencesRepository
import javax.inject.Inject

/**
 * UseCase для получения текущего цвета темы.
 */
class GetThemeColorUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke() = repository.themeColor
}