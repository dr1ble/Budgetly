package shmr.budgetly.domain.usecase

import shmr.budgetly.domain.repository.UserPreferencesRepository
import javax.inject.Inject

/**
 * UseCase для включения или выключения тактильной обратной связи.
 */
class SetHapticsEnabledUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(isEnabled: Boolean) = repository.setHapticsEnabled(isEnabled)
}