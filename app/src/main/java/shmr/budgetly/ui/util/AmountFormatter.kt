package shmr.budgetly.ui.util

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Форматирует денежную сумму для отображения в UI.
 * - Добавляет разделители тысяч (пробелы).
 * - Всегда отображает два знака после точки.
 *
 * @param amount Сумма для форматирования.
 * @param currencySymbol Символ валюты, добавляемый в конце.
 * @return Отформатированная строка, например "1 234.50 ₽".
 */
fun formatAmount(amount: BigDecimal, currencySymbol: String): String {
    val symbols = DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = ' ' // Используем пробел как разделитель групп
    }
    // Формат с группировкой и двумя обязательными знаками после точки
    val formatter = DecimalFormat("#,##0.00", symbols)
    return "${formatter.format(amount)} $currencySymbol"
}

/**
 * Форматирует денежную сумму из строки для отображения в UI.
 * Безопасно парсит строку в BigDecimal и передает в основной форматер.
 *
 * @param amountString Строка с суммой.
 * @param currencySymbol Символ валюты.
 * @return Отформатированная строка или "0.00 ₽", если парсинг не удался.
 */
fun formatAmount(amountString: String, currencySymbol: String): String {
    val amount = try {
        BigDecimal(amountString.replace(',', '.'))
    } catch (_: NumberFormatException) {
        BigDecimal.ZERO
    }
    return formatAmount(amount, currencySymbol)
}

/**
 * Обрезает строку для превью.
 * 1. Ищет первый перенос строки ('\n'). Если находит, берет текст до него.
 *    - Если после переноса был еще текст, добавляет "...".
 * 2. Если переносов нет, обрезает строку по `maxLength` и добавляет "...".
 *
 * @param text Исходный текст.
 * @param maxLength Максимальная длина текста (без учета многоточия), если нет переносов.
 * @return Отформатированная строка для превью.
 */
fun truncateWithEllipsis(text: String, maxLength: Int = 15): String {
    val lineBreakIndex = text.indexOf('\n')

    if (lineBreakIndex != -1) {
        // Найден перенос строки
        val firstLine = text.substring(0, lineBreakIndex)
        // Если после переноса строки есть еще символы (не только пробелы)
        val hasMoreText = text.substring(lineBreakIndex + 1).isNotBlank()
        return if (hasMoreText) "$firstLine..." else firstLine
    }

    // Если переносов нет, работает старая логика
    if (text.length <= maxLength) {
        return text
    }
    return "${text.take(maxLength)}..."
}