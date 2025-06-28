package shmr.budgetly.ui.util

/**
 * Преобразует строковый код валюты в ее символ.
 * @param currencyCode Трехбуквенный код валюты (например, "RUB").
 * @return Символ валюты или исходный код, если символ не найден.
 */
fun formatCurrencySymbol(currencyCode: String): String {
    return when (currencyCode.uppercase()) {
        "RUB" -> "₽"
        "USD" -> "$"
        "EUR" -> "€"
        else -> currencyCode
    }
}