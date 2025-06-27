package shmr.budgetly.data.mapper

import shmr.budgetly.data.network.dto.AccountDto
import shmr.budgetly.data.network.dto.CategoryDto
import shmr.budgetly.data.network.dto.TransactionResponseDto
import shmr.budgetly.domain.entity.Account
import shmr.budgetly.domain.entity.Category
import shmr.budgetly.domain.entity.Transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Преобразует DTO транзакции в доменную сущность Transaction.
 */
fun TransactionResponseDto.toDomainModel(): Transaction {
    return Transaction(
        id = this.id,
        category = this.category.toDomainModel(),
        amount = "${this.amount} ₽",
        transactionDate = LocalDateTime.parse(
            this.transactionDate,
            DateTimeFormatter.ISO_DATE_TIME
        ),
        comment = this.comment.orEmpty()
    )
}

/**
 * Преобразует DTO счета в доменную сущность Account.
 */
fun AccountDto.toDomainModel(): Account {
    return Account(
        id = this.id,
        name = this.name,
        balance = this.balance,
        currency = this.currency
    )
}

/**
 * Преобразует DTO категории в доменную сущность Category.
 */
fun CategoryDto.toDomainModel(): Category {
    return Category(
        id = this.id,
        name = this.name,
        emoji = this.emoji,
        isIncome = this.isIncome
    )
}