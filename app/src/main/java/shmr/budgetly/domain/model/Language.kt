package shmr.budgetly.domain.model

/**
 * Представляет поддерживаемые языки в приложении.
 * @param code ISO 639-1 код языка.
 * @param nativeName Название языка на нем самом.
 */
enum class Language(val code: String, val nativeName: String) {
    RUSSIAN("ru", "Русский"),
    ENGLISH("en", "English");

    companion object {
        fun fromCode(code: String?): Language {
            return entries.find { it.code == code } ?: RUSSIAN // Русский по умолчанию
        }
    }
}