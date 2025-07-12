package shmr.budgetly.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) для ответа на создание/обновление транзакции.
 * Соответствует схеме `Transaction` в OpenAPI.
 */
@Serializable
data class TransactionDto(
    @SerialName("id") val id: Int,
    @SerialName("accountId") val accountId: Int,
    @SerialName("categoryId") val categoryId: Int,
    @SerialName("amount") val amount: String,
    @SerialName("transactionDate") val transactionDate: String,
    @SerialName("comment") val comment: String? = null
)