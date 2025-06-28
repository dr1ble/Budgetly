package shmr.budgetly.domain.repository

import shmr.budgetly.domain.entity.Category
import shmr.budgetly.domain.util.Result

/**
 * Репозиторий для управления данными категорий.
 * Абстрагирует доменный слой от источников данных.
 */
interface CategoryRepository {
    /**
     * Получает список всех категорий.
     * @return [Result] со списком [Category] в случае успеха или [DomainError] в случае ошибки.
     */
    suspend fun getAllCategories(): Result<List<Category>>
}