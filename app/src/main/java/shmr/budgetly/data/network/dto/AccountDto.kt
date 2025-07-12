package shmr.budgetly.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) для полной информации о счете.
 * Используется в ответах, где требуется полная модель счета.
 * Соответствует схеме `Account` в OpenAPI.
 */
@Serializable
data class AccountDto(
    @SerialName("id") val id: Int,
    @SerialName("userId") val userId: Int,
    @SerialName("name") val name: String,
    @SerialName("balance") val balance: String,
    @SerialName("currency") val currency: String
)