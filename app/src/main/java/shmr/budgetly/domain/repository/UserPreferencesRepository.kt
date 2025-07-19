package shmr.budgetly.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Репозиторий для управления пользовательскими настройками.
 */
interface UserPreferencesRepository {
    /**
     * Поток с временной меткой последней успешной синхронизации.
     */
    val lastSyncTimestamp: Flow<Long>

    /**
     * Обновляет временную метку последней успешной синхронизации.
     * @param timestamp Новая временная метка в миллисекундах.
     */
    suspend fun updateLastSyncTimestamp(timestamp: Long)
}