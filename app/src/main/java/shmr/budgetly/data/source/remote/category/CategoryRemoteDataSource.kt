package shmr.budgetly.data.source.remote.category

import shmr.budgetly.data.network.dto.CategoryDto

interface CategoryRemoteDataSource {
    suspend fun getAllCategories(): List<CategoryDto>
}