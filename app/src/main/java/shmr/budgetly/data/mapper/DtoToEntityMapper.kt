package shmr.budgetly.data.mapper

import shmr.budgetly.data.local.model.AccountEntity
import shmr.budgetly.data.local.model.CategoryEntity
import shmr.budgetly.data.local.model.TransactionEntity
import shmr.budgetly.data.network.dto.AccountDto
import shmr.budgetly.data.network.dto.AccountResponseDto
import shmr.budgetly.data.network.dto.CategoryDto
import shmr.budgetly.data.network.dto.TransactionDto
import shmr.budgetly.data.network.dto.TransactionResponseDto
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Преобразует DTO в сущности базы данных.
 */

fun AccountResponseDto.toEntity(
    lastUpdated: Long = System.currentTimeMillis(),
    isDirty: Boolean = false
): AccountEntity {
    return AccountEntity(
        id = this.id,
        name = this.name,
        balance = this.balance,
        currency = this.currency,
        lastUpdated = lastUpdated,
        isDirty = isDirty
    )
}

fun AccountDto.toEntity(
    lastUpdated: Long = System.currentTimeMillis(),
    isDirty: Boolean = false
): AccountEntity {
    return AccountEntity(
        id = this.id,
        name = this.name,
        balance = this.balance,
        currency = this.currency,
        lastUpdated = lastUpdated,
        isDirty = isDirty
    )
}

fun CategoryDto.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = this.id,
        name = this.name,
        emoji = this.emoji,
        isIncome = this.isIncome
    )
}

fun TransactionResponseDto.toEntity(
    lastUpdated: Long = System.currentTimeMillis(),
    isDirty: Boolean = false,
    isDeleted: Boolean = false
): TransactionEntity {
    return TransactionEntity(
        id = this.id,
        accountId = this.account.id,
        categoryId = this.category.id,
        amount = this.amount,
        currency = this.account.currency,
        transactionDate = LocalDateTime.parse(
            this.transactionDate,
            DateTimeFormatter.ISO_DATE_TIME
        ),
        comment = this.comment.orEmpty(),
        lastUpdated = lastUpdated,
        isDirty = isDirty,
        isDeleted = isDeleted
    )
}

/**
 * Преобразует "плоский" [TransactionDto] в сущность БД [TransactionEntity].
 * Так как DTO не содержит всей нужной информации (например, валюты),
 * он использует данные из "старой" локальной сущности.
 * @param sourceEntity "грязная" локальная сущность, из которой берутся недостающие поля.
 */
fun TransactionDto.toEntity(sourceEntity: TransactionEntity): TransactionEntity {
    return sourceEntity.copy(
        id = this.id, // ID берем от сервера
        lastUpdated = System.currentTimeMillis(),
        isDirty = false, // Транзакция теперь синхронизирована
        isDeleted = false
    )
}