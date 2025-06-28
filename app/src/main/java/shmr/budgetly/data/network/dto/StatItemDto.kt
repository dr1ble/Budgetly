package shmr.budgetly.data.network.dto

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) для элемента статистики.
 * Представляет собой сгруппированную сумму по определенной категории.
 */
@Serializable
data class StatItemDto(
    val categoryId: Int,
    val categoryName: String,
    val emoji: String,
    val amount: String
)
