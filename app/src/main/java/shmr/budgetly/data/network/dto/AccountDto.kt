package shmr.budgetly.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class AccountDto(
    val id: Int,
    val name: String,
    val balance: String,
    val currency: String
)