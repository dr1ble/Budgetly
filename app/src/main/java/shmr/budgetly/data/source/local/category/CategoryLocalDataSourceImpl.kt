package shmr.budgetly.data.source.local.category

import kotlinx.coroutines.flow.Flow
import shmr.budgetly.data.local.dao.CategoryDao
import shmr.budgetly.data.local.model.CategoryEntity
import shmr.budgetly.di.scope.AppScope
import javax.inject.Inject

@AppScope
class CategoryLocalDataSourceImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryLocalDataSource {

    override fun getAllCategories(): Flow<List<CategoryEntity>> =
        categoryDao.getAllCategories()

    override suspend fun upsertAll(categories: List<CategoryEntity>) =
        categoryDao.upsertAll(categories)

    override suspend fun getCategoryById(id: Int): CategoryEntity? =
        categoryDao.getCategoryById(id)
}