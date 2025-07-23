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
}