package shmr.budgetly.domain.entity

/**
 * Представляет категорию транзакции в доменном слое.
 * @param id Уникальный идентификатор категории.
 * @param name Название категории.
 * @param emoji Эмодзи, представляющий категорию.
 * @param isIncome Флаг, указывающий, является ли категория доходной.
 */
data class Category(
    val id: Int,
    val name: String,
    val emoji: String,
    val isIncome: Boolean
)