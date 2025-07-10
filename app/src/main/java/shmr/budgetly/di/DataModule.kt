package shmr.budgetly.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import shmr.budgetly.data.repository.AccountRepositoryImpl
import shmr.budgetly.data.repository.CategoryRepositoryImpl
import shmr.budgetly.data.repository.TransactionRepositoryImpl
import shmr.budgetly.data.source.remote.RemoteDataSource
import shmr.budgetly.data.source.remote.RemoteDataSourceImpl
import shmr.budgetly.data.source.remote.account.AccountRemoteDataSource
import shmr.budgetly.data.source.remote.account.AccountRemoteDataSourceImpl
import shmr.budgetly.data.source.remote.category.CategoryRemoteDataSource
import shmr.budgetly.data.source.remote.category.CategoryRemoteDataSourceImpl
import shmr.budgetly.data.source.remote.transaction.TransactionRemoteDataSource
import shmr.budgetly.data.source.remote.transaction.TransactionRemoteDataSourceImpl
import shmr.budgetly.domain.repository.AccountRepository
import shmr.budgetly.domain.repository.CategoryRepository
import shmr.budgetly.domain.repository.TransactionRepository
import javax.inject.Singleton

/**
 * Модуль Hilt для предоставления зависимостей слоя данных.
 * Отвечает за связывание (binding) интерфейсов репозиториев и источников данных
 * с их конкретными реализациями.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindAccountRepository(impl: AccountRepositoryImpl): AccountRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindRemoteDataSource(impl: RemoteDataSourceImpl): RemoteDataSource

    @Binds
    @Singleton
    abstract fun bindAccountRemoteDataSource(impl: AccountRemoteDataSourceImpl): AccountRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindTransactionRemoteDataSource(impl: TransactionRemoteDataSourceImpl): TransactionRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindCategoryRemoteDataSource(impl: CategoryRemoteDataSourceImpl): CategoryRemoteDataSource
}