package shmr.budgetly.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import shmr.budgetly.domain.entity.Account

/**
 * Сущность Room для хранения данных о счете.
 */
@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val balance: String,
    val currency: String,
    val lastUpdated: Long, // Временная метка последнего обновления
    val isDirty: Boolean   // Флаг, указывающий на локальные изменения для синхронизации
)

/**
 * Преобразует сущность базы данных в доменную модель.
 */
fun AccountEntity.toDomainModel(): Account {
    return Account(
        id = this.id,
        name = this.name,
        balance = this.balance,
        currency = this.currency
    )
}

/**
 * Преобразует доменную модель в сущность базы данных.
 */
fun Account.toEntity(
    isDirty: Boolean = false,
    lastUpdated: Long = System.currentTimeMillis()
): AccountEntity {
    return AccountEntity(
        id = this.id,
        name = this.name,
        balance = this.balance,
        currency = this.currency,
        isDirty = isDirty,
        lastUpdated = lastUpdated
    )
}