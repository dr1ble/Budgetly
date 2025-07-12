package shmr.budgetly.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) для элемента статистики.
 * Представляет собой сгруппированную сумму по определенной категории.
 */
@Serializable
data class StatItemDto(
    @SerialName("categoryId") val categoryId: Int,
    @SerialName("categoryName") val categoryName: String,
    @SerialName("emoji") val emoji: String,
    @SerialName("amount") val amount: String
)
