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

    /**
     * Поток, сообщающий, включена ли темная тема.
     * @return Flow<Boolean> true, если темная тема включена, иначе false.
     */
    val isDarkThemeEnabled: Flow<Boolean>

    /**
     * Сохраняет выбор темы пользователя.
     * @param isEnabled true, если темная тема должна быть включена.
     */
    suspend fun setDarkTheme(isEnabled: Boolean)
}