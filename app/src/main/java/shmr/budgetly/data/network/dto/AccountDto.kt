package shmr.budgetly.data.network.dto

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) для данных счета, полученных от сервера.
 * Представляет структуру JSON-ответа для счета.
 */
@Serializable
data class AccountDto(
    val id: Int,
    val name: String,
    val balance: String,
    val currency: String
)