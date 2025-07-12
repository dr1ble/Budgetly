package shmr.budgetly.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Data Transfer Object (DTO) для краткой информации о счете.
 * Используется во вложенных структурах, например, в ответе на запрос транзакции,
 * чтобы не передавать полную модель счета.
 */
@Serializable
data class AccountBriefDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("balance") val balance: String,
    @SerialName("currency") val currency: String
)