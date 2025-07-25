package shmr.budgetly.data.source.local.category

import kotlinx.coroutines.flow.Flow
import shmr.budgetly.data.local.model.CategoryEntity

interface CategoryLocalDataSource {
    fun getAllCategories(): Flow<List<CategoryEntity>>
    suspend fun upsertAll(categories: List<CategoryEntity>)
    suspend fun getCategoryById(id: Int): CategoryEntity?
}