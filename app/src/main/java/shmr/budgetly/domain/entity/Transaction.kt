package shmr.budgetly.domain.entity

import java.time.LocalDateTime

/**
 * Представляет транзакцию в доменном слое.
 * @param id Уникальный идентификатор транзакции.
 * @param category Категория, к которой относится транзакция.
 * @param amount Сумма транзакции в виде строки (например, "500.00").
 * @param currency Трехбуквенный код валюты (например, "RUB").
 * @param transactionDate Дата и время совершения транзакции.
 * @param comment Комментарий к транзакции.
 */
data class Transaction(
    val id: Int,
    val category: Category,
    val amount: String,
    val currency: String,
    val transactionDate: LocalDateTime,
    val comment: String = ""
)