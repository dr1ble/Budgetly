package shmr.budgetly.domain.entity

data class Transaction(
    val id: Int,
    val category: Category,
    val amount: String,
    val comment: String = ""
)