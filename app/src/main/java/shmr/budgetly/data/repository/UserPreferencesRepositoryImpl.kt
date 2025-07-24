package shmr.budgetly.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import shmr.budgetly.di.scope.AppScope
import shmr.budgetly.domain.model.HapticEffect
import shmr.budgetly.domain.model.Language
import shmr.budgetly.domain.model.SyncInterval
import shmr.budgetly.domain.model.ThemeColor
import shmr.budgetly.domain.repository.UserPreferencesRepository
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@AppScope
class UserPreferencesRepositoryImpl @Inject constructor(
    private val context: Context
) : UserPreferencesRepository {

    private object PreferencesKeys {
        val LAST_SYNC_TIMESTAMP = longPreferencesKey("last_sync_timestamp")
        val IS_DARK_THEME_ENABLED = booleanPreferencesKey("is_dark_theme_enabled")
        val THEME_COLOR = stringPreferencesKey("theme_color")
        val IS_HAPTICS_ENABLED = booleanPreferencesKey("is_haptics_enabled")
        val HAPTIC_EFFECT = stringPreferencesKey("haptic_effect")
        val SYNC_INTERVAL = stringPreferencesKey("sync_interval")
        val APP_LANGUAGE = stringPreferencesKey("app_language")
    }

    override val lastSyncTimestamp: Flow<Long> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.LAST_SYNC_TIMESTAMP] ?: 0L
        }

    override suspend fun updateLastSyncTimestamp(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_SYNC_TIMESTAMP] = timestamp
        }
    }

    override val isDarkThemeEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.IS_DARK_THEME_ENABLED] ?: false
        }


    override suspend fun setDarkTheme(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DARK_THEME_ENABLED] = isEnabled
        }
    }

    override val themeColor: Flow<ThemeColor> = context.dataStore.data
        .map { preferences ->
            val colorName = preferences[PreferencesKeys.THEME_COLOR]
            return@map try {
                if (colorName != null) ThemeColor.valueOf(colorName) else ThemeColor.GREEN
            } catch (e: IllegalArgumentException) {
                ThemeColor.GREEN
            }
        }

    override suspend fun setThemeColor(color: ThemeColor) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_COLOR] = color.name
        }
    }

    override val isHapticsEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.IS_HAPTICS_ENABLED] ?: true
        }

    override suspend fun setHapticsEnabled(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_HAPTICS_ENABLED] = isEnabled
        }
    }

    override val hapticEffect: Flow<HapticEffect> = context.dataStore.data
        .map { preferences ->
            val effectName = preferences[PreferencesKeys.HAPTIC_EFFECT]
            return@map try {
                if (effectName != null) HapticEffect.valueOf(effectName) else HapticEffect.CLICK
            } catch (e: IllegalArgumentException) {
                HapticEffect.CLICK
            }
        }

    override suspend fun setHapticEffect(effect: HapticEffect) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAPTIC_EFFECT] = effect.name
        }
    }

    override val syncInterval: Flow<SyncInterval> = context.dataStore.data
        .map { preferences ->
            SyncInterval.fromString(preferences[PreferencesKeys.SYNC_INTERVAL])
        }

    override suspend fun setSyncInterval(interval: SyncInterval) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SYNC_INTERVAL] = interval.name
        }
    }

    override val language: Flow<Language> = context.dataStore.data
        .map { preferences ->
            Language.fromCode(preferences[PreferencesKeys.APP_LANGUAGE])
        }

    override suspend fun setLanguage(language: Language) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_LANGUAGE] = language.code
        }
    }
}