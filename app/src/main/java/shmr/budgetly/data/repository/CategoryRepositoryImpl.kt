package shmr.budgetly.data.repository

import shmr.budgetly.data.mapper.toDomainModel
import shmr.budgetly.data.source.remote.category.CategoryRemoteDataSource
import shmr.budgetly.data.util.safeApiCall
import shmr.budgetly.di.scope.AppScope
import shmr.budgetly.domain.entity.Category
import shmr.budgetly.domain.repository.CategoryRepository
import shmr.budgetly.domain.util.Result
import javax.inject.Inject

/**
 * Реализация [CategoryRepository], отвечающая за получение данных о категориях.
 * Делегирует сетевые вызовы удаленному источнику данных и преобразует DTO в доменные сущности.
 */
@AppScope
class CategoryRepositoryImpl @Inject constructor(
    private val remoteDataSource: CategoryRemoteDataSource // <-- ИЗМЕНЕНИЕ
) : CategoryRepository {

    override suspend fun getAllCategories(): Result<List<Category>> {
        return safeApiCall {
            remoteDataSource.getAllCategories().map { it.toDomainModel() }
        }
    }
}