package shmr.budgetly.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class TransactionResponseDto(
    val id: Int,
    val category: CategoryDto,
    val amount: String,
    val transactionDate: String,
    val comment: String? = null
)