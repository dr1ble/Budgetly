package shmr.budgetly.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import shmr.budgetly.data.local.model.CategoryEntity

/**
 * DAO для работы с сущностью CategoryEntity.
 */
@Dao
interface CategoryDao {
    /**
     * Вставляет или обновляет список категорий.
     */
    @Upsert
    suspend fun upsertAll(categories: List<CategoryEntity>)

    /**
     * Получает все категории.
     */
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    /**
     * Получает категорию по ID.
     */
    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Int): CategoryEntity?
}