package shmr.budgetly.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import shmr.budgetly.BuildConfig
import javax.inject.Named
import javax.inject.Singleton

/**
 * Модуль Hilt для предоставления зависимостей, специфичных для приложения.
 * Отвечает за поставку конфигурационных значений из [BuildConfig],
 * таких как API-токен и флаги, отвязывая компоненты от прямого доступа к нему.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Предоставляет API-токен как именованную зависимость.
     */
    @Provides
    @Singleton
    @Named("apiToken")
    fun provideApiToken(): String = BuildConfig.API_TOKEN

    /**
     * Предоставляет флаг для использования захардкоженного ID счета.
     */
    @Provides
    @Singleton
    @Named("useHardcodedAccountId")
    fun provideUseHardcodedAccountId(): Boolean = BuildConfig.USE_HARDCODED_ACCOUNT_ID
}