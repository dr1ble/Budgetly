package shmr.budgetly.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) для полного ответа на запрос счета по ID.
 * Содержит не только данные самого счета, но и связанную статистику
 * по доходам и расходам.
 */
@Serializable
data class AccountResponseDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("balance") val balance: String,
    @SerialName("currency") val currency: String,
    @SerialName("incomeStats") val incomeStats: List<StatItemDto>,
    @SerialName("expenseStats") val expenseStats: List<StatItemDto>
)