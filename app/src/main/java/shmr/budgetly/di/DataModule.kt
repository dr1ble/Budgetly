package shmr.budgetly.di

import dagger.Binds
import dagger.Module
import shmr.budgetly.data.repository.AccountRepositoryImpl
import shmr.budgetly.data.repository.CategoryRepositoryImpl
import shmr.budgetly.data.repository.TransactionRepositoryImpl
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
 * Модуль Dagger для предоставления зависимостей слоя данных.
 * Связывает интерфейсы репозиториев и источников данных с их конкретными реализациями.
 */
@Module
abstract class DataModule {

    @Binds
    @AppScope
    abstract fun bindAccountRepository(
        accountRepositoryImpl: AccountRepositoryImpl
    ): AccountRepository

    @Binds
    @AppScope
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    @AppScope
    abstract fun bindTransactionRepository(
        transactionRepositoryImpl: TransactionRepositoryImpl
    ): TransactionRepository

    @Binds
    @AppScope
    abstract fun bindAccountRemoteDataSource(
        accountRemoteDataSourceImpl: AccountRemoteDataSourceImpl
    ): AccountRemoteDataSource

    @Binds
    @AppScope
    abstract fun bindCategoryRemoteDataSource(
        categoryRemoteDataSourceImpl: CategoryRemoteDataSourceImpl
    ): CategoryRemoteDataSource

    @Binds
    @AppScope
    abstract fun bindTransactionRemoteDataSource(
        transactionRemoteDataSourceImpl: TransactionRemoteDataSourceImpl
    ): TransactionRemoteDataSource
}