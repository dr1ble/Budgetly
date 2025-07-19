package shmr.budgetly.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import shmr.budgetly.data.local.model.CategoryEntity
import shmr.budgetly.data.local.model.toDomainModel
import shmr.budgetly.data.mapper.toEntity
import shmr.budgetly.data.source.local.category.CategoryLocalDataSource
import shmr.budgetly.data.source.remote.category.CategoryRemoteDataSource
import shmr.budgetly.data.util.safeApiCall
import shmr.budgetly.di.scope.AppScope
import shmr.budgetly.domain.entity.Category
import shmr.budgetly.domain.repository.CategoryRepository
import shmr.budgetly.domain.util.Result
import javax.inject.Inject

@AppScope
class CategoryRepositoryImpl @Inject constructor(
    private val remoteDataSource: CategoryRemoteDataSource,
    private val localDataSource: CategoryLocalDataSource
) : CategoryRepository {

    override fun getAllCategories(): Flow<Result<List<Category>>> {
        return localDataSource.getAllCategories()
            .map<List<CategoryEntity>, Result<List<Category>>> { entities ->
                Result.Success(entities.map { it.toDomainModel() })
            }
            .onStart {
                val remoteResult = safeApiCall { remoteDataSource.getAllCategories() }
                if (remoteResult is Result.Success) {
                    localDataSource.upsertAll(remoteResult.data.map { it.toEntity() })
                } else if (remoteResult is Result.Error) {
                    Log.e(
                        "CategoryRepository",
                        "Failed to refresh categories: ${remoteResult.error}"
                    )
                }
            }
    }
}