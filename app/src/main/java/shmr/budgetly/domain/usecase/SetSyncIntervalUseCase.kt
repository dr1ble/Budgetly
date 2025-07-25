package shmr.budgetly.domain.usecase

import shmr.budgetly.domain.model.SyncInterval
import shmr.budgetly.domain.repository.UserPreferencesRepository
import javax.inject.Inject

/**
 * UseCase для сохранения выбранного интервала синхронизации.
 */
class SetSyncIntervalUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(interval: SyncInterval) = repository.setSyncInterval(interval)
}