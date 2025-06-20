package shmr.budgetly.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import shmr.budgetly.data.repository.BudgetlyRepositoryImpl
import shmr.budgetly.domain.repository.BudgetlyRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBudgetlyRepository(
        budgetlyRepositoryImpl: BudgetlyRepositoryImpl
    ): BudgetlyRepository
}