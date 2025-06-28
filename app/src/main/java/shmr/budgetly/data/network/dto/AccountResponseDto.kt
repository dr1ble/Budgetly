package shmr.budgetly.data.network.dto

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) для полного ответа на запрос счета по ID.
 * Содержит не только данные самого счета, но и связанную статистику
 * по доходам и расходам.
 */
@Serializable
data class AccountResponseDto(
    val id: Int,
    val name: String,
    val balance: String,
    val currency: String,
    val incomeStats: List<StatItemDto>,
    val expenseStats: List<StatItemDto>
)