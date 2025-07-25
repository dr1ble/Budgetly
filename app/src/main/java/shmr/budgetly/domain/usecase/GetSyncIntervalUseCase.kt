package shmr.budgetly.domain.usecase

import shmr.budgetly.domain.repository.UserPreferencesRepository
import javax.inject.Inject

/**
 * UseCase для получения текущего интервала синхронизации.
 */
class GetSyncIntervalUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke() = repository.syncInterval
}