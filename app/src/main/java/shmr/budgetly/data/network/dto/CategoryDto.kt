package shmr.budgetly.data.network.dto

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) для данных категории, полученных от сервера.
 * Представляет структуру JSON-ответа для категории.
 */
@Serializable
data class CategoryDto(
    val id: Int,
    val name: String,
    val emoji: String,
    val isIncome: Boolean
)