package shmr.budgetly.di


import dagger.Module
import dagger.Provides
import shmr.budgetly.BuildConfig
import shmr.budgetly.di.scope.AppScope
import javax.inject.Named

/**
 * Модуль Dagger, предоставляющий зависимости уровня приложения,
 * такие как конфигурационные значения из BuildConfig и контекст приложения.
 */
@Module
object AppModule {

    /**
     * Предоставляет API-токен для авторизации в сети.
     */
    @Provides
    @AppScope
    @Named("apiToken")
    fun provideApiToken(): String = BuildConfig.API_TOKEN

    /**
     * Предоставляет флаг, указывающий, следует ли использовать
     * захардкоженный ID аккаунта для отладки.
     */
    @Provides
    @AppScope
    @Named("useHardcodedAccountId")
    fun provideUseHardcodedAccountId(): Boolean = BuildConfig.USE_HARDCODED_ACCOUNT_ID
}