package shmr.budgetly.data.network.dto

import kotlinx.serialization.Serializable


/**
 * Data Transfer Object (DTO) для краткой информации о счете.
 * Используется во вложенных структурах, например, в ответе на запрос транзакции,
 * чтобы не передавать полную модель счета.
 */
@Serializable
data class AccountBriefDto(
    val id: Int,
    val name: String,
    val balance: String,
    val currency: String
)