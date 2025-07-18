package shmr.budgetly.di

import dagger.Binds
import dagger.Module
import shmr.budgetly.data.repository.AccountRepositoryImpl
import shmr.budgetly.data.repository.CategoryRepositoryImpl
import shmr.budgetly.data.repository.TransactionRepositoryImpl
import shmr.budgetly.data.source.local.account.AccountLocalDataSource
import shmr.budgetly.data.source.local.account.AccountLocalDataSourceImpl
import shmr.budgetly.data.source.local.category.CategoryLocalDataSource
import shmr.budgetly.data.source.local.category.CategoryLocalDataSourceImpl
import shmr.budgetly.data.source.local.transaction.TransactionLocalDataSource
import shmr.budgetly.data.source.local.transaction.TransactionLocalDataSourceImpl
import shmr.budgetly.data.source.remote.account.AccountRemoteDataSource
import shmr.budgetly.data.source.remote.account.AccountRemoteDataSourceImpl
import shmr.budgetly.data.source.remote.category.CategoryRemoteDataSource
import shmr.budgetly.data.source.remote.category.CategoryRemoteDataSourceImpl
import shmr.budgetly.data.source.remote.transaction.TransactionRemoteDataSource
import shmr.budgetly.data.source.remote.transaction.TransactionRemoteDataSourceImpl
import shmr.budgetly.di.scope.AppScope
import shmr.budgetly.domain.repository.AccountRepository
import shmr.budgetly.domain.repository.CategoryRepository
import shmr.budgetly.domain.repository.TransactionRepository

/**
 * Модуль Dagger, предоставляющий реализации репозиториев и источников данных.
 */
@Module
abstract class DataModule {

    // Репозитории
    @Binds
    @AppScope
    abstract fun bindAccountRepository(impl: AccountRepositoryImpl): AccountRepository

    @Binds
    @AppScope
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @AppScope
    abstract fun bindTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository

    // Удаленные источники данных
    @Binds
    @AppScope
    abstract fun bindAccountRemoteDataSource(impl: AccountRemoteDataSourceImpl): AccountRemoteDataSource

    @Binds
    @AppScope
    abstract fun bindCategoryRemoteDataSource(impl: CategoryRemoteDataSourceImpl): CategoryRemoteDataSource

    @Binds
    @AppScope
    abstract fun bindTransactionRemoteDataSource(impl: TransactionRemoteDataSourceImpl): TransactionRemoteDataSource

    // Локальные источники данных
    @Binds
    @AppScope
    abstract fun bindAccountLocalDataSource(impl: AccountLocalDataSourceImpl): AccountLocalDataSource

    @Binds
    @AppScope
    abstract fun bindCategoryLocalDataSource(impl: CategoryLocalDataSourceImpl): CategoryLocalDataSource

    @Binds
    @AppScope
    abstract fun bindTransactionLocalDataSource(impl: TransactionLocalDataSourceImpl): TransactionLocalDataSource
}