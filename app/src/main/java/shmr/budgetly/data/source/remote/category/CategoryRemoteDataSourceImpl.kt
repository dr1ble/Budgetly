package shmr.budgetly.data.source.remote.category

import shmr.budgetly.data.network.ApiService
import shmr.budgetly.data.network.dto.CategoryDto
import shmr.budgetly.di.scope.AppScope
import javax.inject.Inject

@AppScope
class CategoryRemoteDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : CategoryRemoteDataSource {
    override suspend fun getAllCategories(): List<CategoryDto> = apiService.getAllCategories()
}