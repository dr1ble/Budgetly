package shmr.budgetly.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import shmr.budgetly.data.network.ApiService
import shmr.budgetly.data.network.interceptors.AuthInterceptor
import shmr.budgetly.data.network.interceptors.ConnectivityInterceptor
import shmr.budgetly.data.network.interceptors.RetryInterceptor
import shmr.budgetly.di.scope.AppScope
import javax.inject.Named

/**
 * Модуль Dagger для предоставления зависимостей сетевого слоя.
 * Отвечает за конфигурацию и создание [OkHttpClient], [Retrofit] и [ApiService].
 */
@Module
object NetworkModule {
    private const val BASE_URL = "https://shmr-finance.ru/api/v1/"

    @Provides
    @AppScope
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @AppScope
    fun provideAuthInterceptor(@Named("apiToken") authToken: String?): AuthInterceptor {
        return AuthInterceptor(authToken)
    }

    @Provides
    @AppScope
    fun provideConnectivityInterceptor(context: Context): ConnectivityInterceptor {
        return ConnectivityInterceptor(context)
    }

    @Provides
    @AppScope
    fun provideRetryInterceptor(): RetryInterceptor {
        return RetryInterceptor()
    }

    @Provides
    @AppScope
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        connectivityInterceptor: ConnectivityInterceptor,
        retryInterceptor: RetryInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(connectivityInterceptor) // 1. Проверка сети
        .addInterceptor(retryInterceptor)        // 2. Повторные запросы
        .addInterceptor(authInterceptor)         // 3. Добавление токена
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    @Provides
    @AppScope
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @AppScope
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}