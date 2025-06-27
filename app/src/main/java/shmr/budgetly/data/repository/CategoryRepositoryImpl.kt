package shmr.budgetly.data.repository

import shmr.budgetly.data.mapper.toDomainModel
import shmr.budgetly.data.source.remote.RemoteDataSource
import shmr.budgetly.data.util.safeApiCall
import shmr.budgetly.domain.entity.Category
import shmr.budgetly.domain.repository.CategoryRepository
import shmr.budgetly.domain.util.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : CategoryRepository {

    override suspend fun getAllCategories(): Result<List<Category>> {
        return safeApiCall {
            remoteDataSource.getAllCategories().map { it.toDomainModel() }
        }
    }
}