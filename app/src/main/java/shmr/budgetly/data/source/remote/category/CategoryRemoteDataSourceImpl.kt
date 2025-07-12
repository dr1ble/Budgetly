package shmr.budgetly.data.source.remote.category

import shmr.budgetly.data.network.ApiService
import shmr.budgetly.data.network.dto.CategoryDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRemoteDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : CategoryRemoteDataSource {
    override suspend fun getAllCategories(): List<CategoryDto> = apiService.getAllCategories()
}