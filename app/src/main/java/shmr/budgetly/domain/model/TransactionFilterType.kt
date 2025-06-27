package shmr.budgetly.domain.model

/**
 * Определяет тип фильтрации для списка транзакций.
 */
enum class TransactionFilterType {
    EXPENSE,
    INCOME,
    ALL
}