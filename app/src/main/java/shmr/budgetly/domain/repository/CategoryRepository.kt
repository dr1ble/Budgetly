package shmr.budgetly.domain.repository

import shmr.budgetly.domain.entity.Category
import shmr.budgetly.domain.util.Result

/**
 * Репозиторий для управления данными категорий.
 */
interface CategoryRepository {
    suspend fun getAllCategories(): Result<List<Category>>
}