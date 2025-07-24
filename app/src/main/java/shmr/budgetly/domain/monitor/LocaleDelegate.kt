package shmr.budgetly.domain.monitor

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import shmr.budgetly.di.scope.AppScope
import shmr.budgetly.domain.repository.UserPreferencesRepository
import javax.inject.Inject


/**
 * Управляет установкой локали для всего приложения.
 */
@AppScope
class LocaleDelegate @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    /**
     * Асинхронно загружает сохраненную локаль и применяет ее,
     */
    fun applyLocale() {
        scope.launch {
            val desiredLanguage = userPreferencesRepository.language.first()
            val desiredLocaleList = LocaleListCompat.forLanguageTags(desiredLanguage.code)

            val currentLocales = AppCompatDelegate.getApplicationLocales()

            if (currentLocales != desiredLocaleList) {
                AppCompatDelegate.setApplicationLocales(desiredLocaleList)
            }
        }
    }
}