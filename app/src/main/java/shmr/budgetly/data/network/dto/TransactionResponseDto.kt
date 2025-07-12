package shmr.budgetly.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) для ответа на запрос транзакции.
 * Содержит полную информацию о транзакции, включая вложенные объекты счета и категории.
 * Соответствует схеме `TransactionResponse` в OpenAPI.
 */
@Serializable
data class TransactionResponseDto(
    @SerialName("id") val id: Int,
    @SerialName("account") val account: AccountBriefDto,
    @SerialName("category") val category: CategoryDto,
    @SerialName("amount") val amount: String,
    @SerialName("transactionDate") val transactionDate: String,
    @SerialName("comment") val comment: String? = null
)
