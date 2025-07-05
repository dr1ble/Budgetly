package shmr.budgetly.data.network.dto

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) для запроса на обновление счета.
 * Содержит поля, которые могут быть изменены пользователем.
 */
@Serializable
data class UpdateAccountRequestDto(
    val name: String,
    val balance: String,
    val currency: String
)