package shmr.budgetly.ui.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * Утилитный объект для форматирования дат на экране "История".
 * Инкапсулирует логику преобразования дат в локализованные строки.
 */
object HistoryDateFormatter {

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val russianLocale = Locale("ru")

    private val monthsInGenitiveCase = mapOf(
        1 to "января", 2 to "февраля", 3 to "марта", 4 to "апреля", 5 to "мая",
        6 to "июня", 7 to "июля", 8 to "августа", 9 to "сентября", 10 to "октября",
        11 to "ноября", 12 to "декабря"
    )

    /**
     * Форматирует дату и время транзакции для отображения в списке.
     * Пример: "15 августа 14:30".
     */
    fun formatTransactionDate(dateTime: LocalDateTime): String {
        val day = dateTime.dayOfMonth
        val month = monthsInGenitiveCase[dateTime.monthValue] ?: ""
        val time = dateTime.format(timeFormatter)
        return "$day $month $time"
    }

    /**
     * Форматирует дату для отображения в заголовке выбора периода.
     * Пример: "15 августа 2023".
     */
    fun formatHeaderDate(date: LocalDate): String {
        val monthName = date.month.getDisplayName(TextStyle.FULL, russianLocale)
        val day = date.dayOfMonth
        val year = date.year
        return "$day $monthName $year"
    }
}