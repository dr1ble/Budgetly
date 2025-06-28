package shmr.budgetly.domain.model

/**
 * Определяет тип фильтрации для списка транзакций, используемый в UseCase'ах.
 */
enum class TransactionFilterType {
    EXPENSE,
    INCOME,
    ALL
}