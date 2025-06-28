package shmr.budgetly.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import shmr.budgetly.domain.repository.AccountRepository
import shmr.budgetly.domain.repository.CategoryRepository
import shmr.budgetly.domain.repository.TransactionRepository
import shmr.budgetly.domain.usecase.GetAllCategoriesUseCase
import shmr.budgetly.domain.usecase.GetExpenseTransactionsUseCase
import shmr.budgetly.domain.usecase.GetHistoryUseCase
import shmr.budgetly.domain.usecase.GetIncomeTransactionsUseCase
import shmr.budgetly.domain.usecase.GetMainAccountUseCase

/**
 * Модуль Hilt для предоставления зависимостей доменного слоя.
 * Отвечает за создание экземпляров UseCase'ов, предоставляя им необходимые репозитории.
 * Зависимости имеют скоуп [ViewModelScoped], так как используются только во ViewModel.
 */
@Module
@InstallIn(ViewModelComponent::class)
object DomainModule {

    @Provides
    @ViewModelScoped
    fun provideGetMainAccountUseCase(repository: AccountRepository): GetMainAccountUseCase =
        GetMainAccountUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideGetExpenseTransactionsUseCase(repository: TransactionRepository): GetExpenseTransactionsUseCase =
        GetExpenseTransactionsUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideGetIncomeTransactionsUseCase(repository: TransactionRepository): GetIncomeTransactionsUseCase =
        GetIncomeTransactionsUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideGetHistoryUseCase(repository: TransactionRepository): GetHistoryUseCase =
        GetHistoryUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideGetAllCategoriesUseCase(repository: CategoryRepository): GetAllCategoriesUseCase =
        GetAllCategoriesUseCase(repository)
}