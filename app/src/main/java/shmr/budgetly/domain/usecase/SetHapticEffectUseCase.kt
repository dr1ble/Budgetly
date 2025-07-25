package shmr.budgetly.domain.usecase

import shmr.budgetly.domain.model.HapticEffect
import shmr.budgetly.domain.repository.UserPreferencesRepository
import javax.inject.Inject

/**
 * UseCase для установки выбранного эффекта тактильной обратной связи.
 */
class SetHapticEffectUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(effect: HapticEffect) = repository.setHapticEffect(effect)
}