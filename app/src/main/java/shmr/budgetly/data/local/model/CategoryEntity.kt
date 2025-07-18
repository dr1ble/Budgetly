package shmr.budgetly.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import shmr.budgetly.domain.entity.Category

/**
 * Сущность Room для хранения данных о категориях.
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val emoji: String,
    val isIncome: Boolean
)

/**
 * Преобразует сущность базы данных в доменную модель.
 */
fun CategoryEntity.toDomainModel(): Category {
    return Category(
        id = this.id,
        name = this.name,
        emoji = this.emoji,
        isIncome = this.isIncome
    )
}

/**
 * Преобразует доменную модель в сущность базы данных.
 */
fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = this.id,
        name = this.name,
        emoji = this.emoji,
        isIncome = this.isIncome
    )
}