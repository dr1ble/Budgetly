package shmr.budgetly.domain.repository

import kotlinx.coroutines.flow.Flow
import shmr.budgetly.domain.entity.Category
import shmr.budgetly.domain.util.Result

/**
 * Репозиторий для управления данными категорий.
 * Абстрагирует доменный слой от источников данных.
 */
interface CategoryRepository {
    /**
     * Получает список всех категорий в виде потока данных.
     * @return [Flow] с [Result], содержащим список [Category] в случае успеха или ошибку.
     */
    fun getAllCategories(): Flow<Result<List<Category>>>
}