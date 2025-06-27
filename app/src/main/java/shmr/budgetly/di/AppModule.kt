package shmr.budgetly.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import shmr.budgetly.BuildConfig
import javax.inject.Named
import javax.inject.Singleton

/**
 * Модуль для предоставления зависимостей, специфичных для приложения,
 * таких как ключи API и флаги конфигурации из BuildConfig.
 * Это позволяет отвязать компоненты от прямого доступа к BuildConfig.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @Named("apiToken")
    fun provideApiToken(): String = BuildConfig.API_TOKEN

    @Provides
    @Singleton
    @Named("useHardcodedAccountId")
    fun provideUseHardcodedAccountId(): Boolean = BuildConfig.USE_HARDCODED_ACCOUNT_ID
}