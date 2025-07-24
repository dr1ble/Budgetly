package shmr.budgetly.domain.repository

import kotlinx.coroutines.flow.Flow
import shmr.budgetly.domain.model.HapticEffect
import shmr.budgetly.domain.model.Language
import shmr.budgetly.domain.model.SyncInterval
import shmr.budgetly.domain.model.ThemeColor

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

    /**
     * Поток с выбранным основным цветом темы.
     */
    val themeColor: Flow<ThemeColor>

    /**
     * Сохраняет выбранный основной цвет темы.
     */
    suspend fun setThemeColor(color: ThemeColor)

    /**
     * Поток, сообщающий, включена ли тактильная обратная связь.
     */
    val isHapticsEnabled: Flow<Boolean>

    /**
     * Сохраняет настройку включения/выключения тактильной обратной связи.
     */
    suspend fun setHapticsEnabled(isEnabled: Boolean)

    /**
     * Поток с выбранным эффектом тактильной обратной связи.
     */
    val hapticEffect: Flow<HapticEffect>

    /**
     * Сохраняет выбранный эффект тактильной обратной связи.
     */
    suspend fun setHapticEffect(effect: HapticEffect)

    /**
     * Поток с выбранным интервалом синхронизации.
     */
    val syncInterval: Flow<SyncInterval>

    /**
     * Сохраняет выбранный интервал синхронизации.
     */
    suspend fun setSyncInterval(interval: SyncInterval)

    /**
     * Поток с выбранным языком приложения.
     */
    val language: Flow<Language>

    /**
     * Сохраняет выбранный язык приложения.
     * @param language Язык для сохранения.
     */
    suspend fun setLanguage(language: Language)
}