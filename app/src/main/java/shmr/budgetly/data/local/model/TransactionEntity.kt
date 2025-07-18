package shmr.budgetly.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import shmr.budgetly.domain.entity.Category
import shmr.budgetly.domain.entity.Transaction
import java.time.LocalDateTime

/**
 * Сущность Room для хранения данных о транзакциях.
 */
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: Int,
    val accountId: Int,
    val categoryId: Int,
    val amount: String,
    val currency: String,
    val transactionDate: LocalDateTime,
    val comment: String,
    val lastUpdated: Long, // Временная метка последнего обновления
    val isDirty: Boolean,  // Флаг, указывающий на локальные изменения
    val isDeleted: Boolean // Флаг для мягкого удаления
)

/**
 * Преобразует сущность базы данных и ее категорию в доменную модель.
 */
fun TransactionEntity.toDomainModel(category: Category): Transaction {
    return Transaction(
        id = this.id,
        category = category,
        amount = this.amount,
        currency = this.currency,
        transactionDate = this.transactionDate,
        comment = this.comment
    )
}

/**
 * Преобразует доменную модель в сущность базы данных.
 */
fun Transaction.toEntity(
    isDirty: Boolean = false,
    isDeleted: Boolean = false,
    lastUpdated: Long = System.currentTimeMillis()
): TransactionEntity {
    return TransactionEntity(
        id = this.id,
        accountId = 0, // Это поле нужно будет заполнять из счета
        categoryId = this.category.id,
        amount = this.amount,
        currency = this.currency,
        transactionDate = this.transactionDate,
        comment = this.comment,
        isDirty = isDirty,
        isDeleted = isDeleted,
        lastUpdated = lastUpdated
    )
}