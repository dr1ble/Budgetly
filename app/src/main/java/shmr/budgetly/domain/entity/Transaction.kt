package shmr.budgetly.domain.entity

import java.time.LocalDateTime

data class Transaction(
    val id: Int,
    val category: Category,
    val amount: String,
    val transactionDate: LocalDateTime,
    val comment: String = ""
)