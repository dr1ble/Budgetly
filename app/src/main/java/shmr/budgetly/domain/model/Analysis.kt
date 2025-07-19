package shmr.budgetly.domain.model

import shmr.budgetly.domain.entity.Category
import java.math.BigDecimal

/**
 * Результат анализа транзакций.
 *
 * @param items Список проанализированных элементов по категориям.
 * @param totalAmount Общая сумма всех транзакций.
 * @param currencySymbol Символ валюты.
 */
data class AnalysisResult(
    val items: List<AnalysisItem>,
    val totalAmount: BigDecimal,
    val currencySymbol: String
)

/**
 * Проанализированный элемент, сгруппированный по категории.
 *
 * @param category Категория.
 * @param totalAmount Сумма по данной категории.
 * @param percentage Процент от общей суммы.
 * @param exampleComment Комментарий из самой последней транзакции в этой категории.
 */
data class AnalysisItem(
    val category: Category,
    val totalAmount: BigDecimal,
    val percentage: Float,
    val exampleComment: String
)