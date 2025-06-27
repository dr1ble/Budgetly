package shmr.budgetly.domain.usecase

import shmr.budgetly.domain.repository.CategoryRepository
import javax.inject.Inject

class GetAllCategoriesUseCase @Inject constructor(private val repository: CategoryRepository) {
    suspend operator fun invoke() = repository.getAllCategories()
}