package shmr.budgetly.data.local.model

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Представляет транзакцию вместе с ее категорией.
 * Используется для JOIN-запросов в Room.
 */
data class TransactionWithCategory(
    @Embedded val transaction: TransactionEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity
)