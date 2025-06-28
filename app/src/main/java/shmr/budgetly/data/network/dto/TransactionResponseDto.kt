package shmr.budgetly.data.network.dto

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) для данных транзакции, полученных от сервера.
 * Представляет структуру JSON-ответа для транзакции.
 */
@Serializable
data class TransactionResponseDto(
    val id: Int,
    val category: CategoryDto,
    val amount: String,
    val transactionDate: String,
    val comment: String? = null
)