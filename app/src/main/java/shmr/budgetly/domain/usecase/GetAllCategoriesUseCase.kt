package shmr.budgetly.domain.usecase

import shmr.budgetly.domain.repository.CategoryRepository
import javax.inject.Inject

/**
 * UseCase для получения списка всех категорий.
 * Инкапсулирует бизнес-логику получения всех категорий, делегируя задачу репозиторию.
 */
class GetAllCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke() = repository.getAllCategories()
}