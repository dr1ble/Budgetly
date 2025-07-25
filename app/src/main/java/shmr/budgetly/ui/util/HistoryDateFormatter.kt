package shmr.budgetly.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Утилитный объект для форматирования дат на экране "История".
 * Инкапсулирует логику преобразования дат в локализованные строки.
 */
object HistoryDateFormatter {

    /**
     * Форматирует дату и время транзакции для отображения в списке.
     * Пример: "15 августа 14:30" или "August 15, 2:30 PM".
     * @param dateTime Дата и время для форматирования.
     * @param locale Текущая локаль для корректного отображения.
     */
    fun formatTransactionDate(dateTime: LocalDateTime, locale: Locale): String {
        val formatter = DateTimeFormatter.ofPattern("d MMMM HH:mm", locale)
        return dateTime.format(formatter)
    }

    /**
     * Форматирует дату для отображения в заголовке выбора периода.
     * Пример: "15 августа 2023" или "August 15, 2023".
     * @param date Дата для форматирования.
     * @param locale Текущая локаль для корректного отображения.
     */
    fun formatHeaderDate(date: LocalDate, locale: Locale): String {
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", locale)
        return date.format(formatter)
    }

    /**
     * Composable-функция для получения текущей локали из контекста.
     */
    @Composable
    fun currentLocale(): Locale {
        return LocalConfiguration.current.locales[0]
    }
}