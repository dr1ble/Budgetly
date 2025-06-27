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
import shmr.budgetly.domain.repository.AccountRepository
import shmr.budgetly.domain.repository.CategoryRepository
import shmr.budgetly.domain.repository.TransactionRepository
import javax.inject.Singleton

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
    abstract fun bindRemoteDataSource(
        remoteDataSourceImpl: RemoteDataSourceImpl
    ): RemoteDataSource
}