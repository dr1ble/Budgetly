package shmr.budgetly.di

import android.content.Context
import dagger.Module
import dagger.Provides
import shmr.budgetly.BuildConfig
import shmr.budgetly.di.scope.AppScope
import shmr.budgetly.domain.monitor.HapticFeedbackManager
import javax.inject.Named

/**
 * Модуль Dagger, предоставляющий зависимости уровня приложения,
 * такие как конфигурационные значения из BuildConfig и контекст приложения.
 */
@Module
abstract class AppModule {

    companion object {
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

        @Provides
        @AppScope
        fun provideHapticFeedbackManager(context: Context): HapticFeedbackManager {
            return HapticFeedbackManager(context)
        }
    }
}