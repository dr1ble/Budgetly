package shmr.budgetly.domain.usecase

import androidx.appcompat.app.AppCompatDelegate
import shmr.budgetly.domain.model.Language
import java.util.Locale
import javax.inject.Inject

/**
 * UseCase для получения текущего языка приложения из AppCompatDelegate.
 */
class GetLanguageUseCase @Inject constructor() {
    operator fun invoke(): Language {
        val locales = AppCompatDelegate.getApplicationLocales()

        val primaryLocale = if (locales.isEmpty) {
            Locale.getDefault()
        } else {
            locales[0]
        }

        return Language.fromCode(primaryLocale?.language)
    }
}