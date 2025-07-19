package shmr.budgetly.domain.usecase

import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.model.AnalysisItem
import shmr.budgetly.domain.model.AnalysisResult
import shmr.budgetly.ui.util.formatCurrencySymbol
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

/**
 * UseCase для анализа списка транзакций.
 * Группирует транзакции по категориям, вычисляет общую сумму и процентное соотношение
 * для каждой категории.
 */
class AnalyzeTransactionsUseCase @Inject constructor() {

    /**
     * @param transactions Список транзакций для анализа.
     * @return [AnalysisResult], содержащий детализированный анализ, или null, если список пуст.
     */
    operator fun invoke(transactions: List<Transaction>): AnalysisResult? {
        if (transactions.isEmpty()) {
            return null
        }

        val totalAmount = transactions.sumOf { it.amount.toBigDecimalOrZero() }
        if (totalAmount == BigDecimal.ZERO) {
            return null
        }

        val currencySymbol = transactions.firstOrNull()?.currency?.let {
            formatCurrencySymbol(it)
        } ?: ""

        val groupedByCategory = transactions.groupBy { it.category }

        val analysisItems = groupedByCategory.map { (category, transactionList) ->
            val categoryTotal = transactionList.sumOf { it.amount.toBigDecimalOrZero() }
            val percentage = categoryTotal.divide(totalAmount, 4, RoundingMode.HALF_UP)
                .toFloat() * 100

            val mostRecentComment = transactionList
                .maxByOrNull { it.transactionDate }
                ?.comment
                ?.takeIf { it.isNotBlank() } ?: ""

            AnalysisItem(category, categoryTotal, percentage, mostRecentComment)
        }.sortedByDescending { it.totalAmount }

        return AnalysisResult(analysisItems, totalAmount, currencySymbol)
    }

    private fun String.toBigDecimalOrZero(): BigDecimal {
        return try {
            BigDecimal(this.replace(',', '.'))
        } catch (e: NumberFormatException) {
            BigDecimal.ZERO
        }
    }
}