package shmr.budgetly.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) для запроса на обновление счета.
 * Содержит поля, которые могут быть изменены пользователем.
 */
@Serializable
data class UpdateAccountRequestDto(
    @SerialName("name") val name: String,
    @SerialName("balance") val balance: String,
    @SerialName("currency") val currency: String
)